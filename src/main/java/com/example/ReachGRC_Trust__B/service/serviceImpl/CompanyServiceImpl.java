package com.example.ReachGRC_Trust__B.service.serviceImpl;

import com.example.ReachGRC_Trust__B.dtos.CompanyDto;
import com.example.ReachGRC_Trust__B.dtos.ControlDto;
import com.example.ReachGRC_Trust__B.dtos.DomainDto;
import com.example.ReachGRC_Trust__B.dtos.MilestoneDto;
import com.example.ReachGRC_Trust__B.dtos.requestDtos.CompanyRequestDto;
import com.example.ReachGRC_Trust__B.entity.Company;
import com.example.ReachGRC_Trust__B.entity.Control;
import com.example.ReachGRC_Trust__B.entity.Domain;
import com.example.ReachGRC_Trust__B.entity.Milestone;
import com.example.ReachGRC_Trust__B.exceptions.DuplicateResourceException;
import com.example.ReachGRC_Trust__B.repository.CompanyRepository;
import com.example.ReachGRC_Trust__B.repository.MilestoneRepository;
import com.example.ReachGRC_Trust__B.service.service.CompanyService;
import com.example.ReachGRC_Trust__B.utils.ExcelHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final MilestoneRepository milestoneRepository;
    private final ModelMapper modelMapper;
    private final ExcelHelper excelHelper;


    @Override
    @Transactional(readOnly = true)
    public CompanyDto getCompanyById(Long id){
        log.info("Fetching company with ID: {}", id);
        Company company = companyRepository.findById(id).orElseThrow(() -> new RuntimeException("company not found"));
        return mapToDto(company);
    }

    @Override
    @Transactional
    public CompanyDto createCompany(CompanyDto companyDto){
        log.info("Creating company: {}", companyDto.getCompanyName());

        if(companyRepository.existsByCompanyName(companyDto.getCompanyName())) {
            throw new DuplicateResourceException("Company with name :" +companyDto.getCompanyName()+ "already exists");
        }

        Company company = mapToEntity(companyDto);
        Company savedCompany = companyRepository.save(company);

        log.info("Company created successfully with ID: {}", savedCompany.getId());
        return mapToDto(savedCompany);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyDto> getAllActiveCompanies(){
        log.info("Fetching all active companies");
        return companyRepository.findByIsActive(true).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyDto> getAllCompanies(){
        log.info("Fetching all companies");

        return companyRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public void softDeleteCompany(Long id){
        log.info("Soft deleting company with ID:{}", id);
        Company company = companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Company not found with ID: " + id));
        company.setIsActive(false);
        companyRepository.save(company);
        log.info("Company soft deleted successfully with ID: {}", id);

    }

    @Override
    @Transactional
    public void activateCompany(Long id){
        log.info("Soft activating company with ID:{}", id);
        Company company = companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Company not found with ID: " + id));
        company.setIsActive(true);
        companyRepository.save(company);
        log.info("Company soft activated successfully with ID: {}", id);

    }


    @Override
    @Transactional
    public List<CompanyDto> importFromExcel(MultipartFile file) throws IOException {
        log.info("Importing companies from Excel file: {}", file.getOriginalFilename());

        List<CompanyDto> companies = excelHelper.parseExcelFile(file);

        List<CompanyDto> savedCompanies = companies.stream()
                .map(companyDto ->{
                    try {
                        if( companyRepository.existsByCompanyName(companyDto.getCompanyName())){
                            log.warn("Company already exists, skipping: {}", companyDto.getCompanyName());
                            return null;
                        }
                        return createCompany(companyDto);

                    } catch (Exception e) {
                        log.error("Error importing company: {}", companyDto.getCompanyName());
                        return null;
                    }
                        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("Successfully imported {} companies", savedCompanies.size());
        return savedCompanies;
    }


    @Override
    @Transactional
    public CompanyDto updateCompany(Long id, CompanyDto companyDto) {
        log.info("Updating company with ID: {}", id);

        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with ID:" + id));

        if (!existingCompany.getCompanyName().equals(companyDto.getCompanyName())) {
            if (companyRepository.existsByCompanyName(companyDto.getCompanyName())) {
                throw new DuplicateResourceException("Company with name: " + companyDto.getCompanyName() + " already exists");
            }
            existingCompany.setCompanyName(companyDto.getCompanyName());
        }

        // Update basic fields
        existingCompany.setStatement(companyDto.getStatement());
        existingCompany.setLatitude(companyDto.getLatitude());
        existingCompany.setLongitude(companyDto.getLongitude());
        existingCompany.setLocationName(companyDto.getLocationName());

        if (companyDto.getIsActive() != null) {
            existingCompany.setIsActive(companyDto.getIsActive());
        }

        // Smart Update for Domains
        if (companyDto.getDomains() != null) {
            updateDomains(existingCompany, companyDto.getDomains());
        } else {
             // If null, do we clear? Or assume no changes? 
             // Existing logic was: existingCompany.getDomains().clear(); if dto.domains was not null?
             // Actually existing logic:
             // existingCompany.getDomains().clear();
             // if(companyDto.getDomains() != null) { ... add all ... }
             // So if null, it cleared everything.
             // We should probably clear if specific list is passed as empty/null to represent "no domains"
             // But usually null means "don't touch". Let's assume we want to sync what defines the company.
             // If the sheet has no domains, we probably want to remove them.
             existingCompany.getDomains().clear();
        }

        // Smart Update for Milestones
        if (companyDto.getMilestones() != null && !companyDto.getMilestones().isEmpty()) {
            updateCompanyMilestones(existingCompany, companyDto.getMilestones());
        }

        Company updatedCompany = companyRepository.save(existingCompany);
        log.info("Company updated successfully with ID: {}", updatedCompany.getId());

        return mapToDto(updatedCompany);
    }

    private void updateDomains(Company company, List<DomainDto> domainDtos) {
        List<Domain> existingDomains = company.getDomains();
        
        // 1. Identify domains to remove (present in DB but not in DTO)
        List<Long> incomingIds = domainDtos.stream()
                .map(DomainDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        existingDomains.removeIf(domain -> domain.getId() != null && !incomingIds.contains(domain.getId()));

        // 2. Identify domains to add or update
        for (DomainDto domainDto : domainDtos) {
            Optional<Domain> existingDomainOpt = Optional.empty();
            if (domainDto.getId() != null) {
                existingDomainOpt = existingDomains.stream()
                        .filter(d -> d.getId().equals(domainDto.getId()))
                        .findFirst();
            }
            if (existingDomainOpt.isEmpty()) {
                existingDomainOpt = existingDomains.stream()
                        .filter(d -> d.getName().equals(domainDto.getName()))
                        .findFirst();
            }

            if (existingDomainOpt.isPresent()) {
                // Update existing domain
                Domain domain = existingDomainOpt.get();
                domain.setName(domainDto.getName());
                if (domainDto.getControls() != null) {
                    updateControls(domain, domainDto.getControls());
                } else {
                    domain.getControls().clear();
                }
            } else {
                // Add new domain
                Domain newDomain = mapDomainToEntity(domainDto);
                company.addDomain(newDomain);
            }
        }
    }

    private void updateControls(Domain domain, List<ControlDto> controlDtos) {
        List<Control> existingControls = domain.getControls();

        // 1. Identify controls to remove
        List<Long> incomingIds = controlDtos.stream()
                .map(ControlDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        existingControls.removeIf(control -> control.getId() != null && !incomingIds.contains(control.getId()));

        // 2. Add or Update
        for (ControlDto controlDto : controlDtos) {
            Optional<Control> existingControlOpt = Optional.empty();
            if (controlDto.getId() != null) {
                existingControlOpt = existingControls.stream()
                        .filter(c -> c.getId().equals(controlDto.getId()))
                        .findFirst();
            }
            if (existingControlOpt.isEmpty()) {
                existingControlOpt = existingControls.stream()
                        .filter(c -> c.getName().equals(controlDto.getName()))
                        .findFirst();
            }

            if (existingControlOpt.isPresent()) {
                Control control = existingControlOpt.get();
                // Update fields
                control.setName(controlDto.getName());
                control.setStatus(controlDto.getStatus());
                control.setRemarks(controlDto.getRemarks());
            } else {
                Control newControl = modelMapper.map(controlDto, Control.class);
                newControl.setId(null);
                domain.addControl(newControl);
            }
        }
    }

    @Override
    @Transactional
    public List<CompanyDto> syncFromExcel(MultipartFile file) throws IOException{
        log.info("Syncing companies from Excel file (upsert mode) : {}", file.getOriginalFilename());

        List<CompanyDto> companies = excelHelper.parseExcelFile(file);
        return syncCompanies(companies);
    }
    
    @Override
    @Transactional
    public List<CompanyDto> syncCompanies(List<CompanyDto> incomingCompanies) {
        List<CompanyDto> processedCompanies = new ArrayList<>();
        int created = 0;
        int updated = 0;
        int deleted = 0;

        // 1. Track incoming company names
        List<String> incomingCompanyNames = incomingCompanies.stream()
                .map(CompanyDto::getCompanyName)
                .collect(Collectors.toList());

        // 2. Upsert (Create or Update)
        for (CompanyDto companyDto : incomingCompanies){
            try {
                Optional<Company> existingCompany = companyRepository.findByCompanyName(companyDto.getCompanyName());

                if(existingCompany.isPresent()) {
                    Company existing = existingCompany.get();
                    // Preserve existing location details since sheet/excel sync does not manage them
                    companyDto.setLatitude(existing.getLatitude());
                    companyDto.setLongitude(existing.getLongitude());
                    companyDto.setLocationName(existing.getLocationName());

                    CompanyDto updatedCompany = updateCompany(existing.getId(), companyDto);
                    processedCompanies.add(updatedCompany);
                    updated++;
                    log.info("Updated company: {}", companyDto.getCompanyName());

                } else {
                    CompanyDto createdCompany = createCompany(companyDto);
                    processedCompanies.add(createdCompany);
                    created++;
                    log.info("Created company: {}", companyDto.getCompanyName());
                }
            } catch (Exception e) {
                log.error("Error syncing company: {}", companyDto.getCompanyName(), e);
            }
        }

        // 3. Handle Deletions (Soft Delete companies not in incoming list)
        // Only consider active companies for deletion check to avoid re-deleting
        List<Company> allActiveCompanies = companyRepository.findByIsActive(true);
        for (Company activeCompany : allActiveCompanies) {
            if (!incomingCompanyNames.contains(activeCompany.getCompanyName())) {
                try {
                    softDeleteCompany(activeCompany.getId());
                    deleted++;
                    log.info("Soft deleted missing company: {}", activeCompany.getCompanyName());
                } catch (Exception e) {
                    log.error("Error soft deleting company: {}", activeCompany.getCompanyName(), e);
                }
            }
        }

        log.info("Sync Completed - Created: {}, Updated: {}, Soft Deleted: {}, Total Processed: {}", created, updated, deleted, processedCompanies.size());
        return processedCompanies;
    }

    @Override
    @Transactional
    public CompanyDto generateApiKey(Long companyId) {
        log.info("Generating API Key for company ID: {}", companyId);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));
        
        String newKey = "rgc_" + java.util.UUID.randomUUID().toString().replace("-", "");
        company.setApiKey(newKey);
        company.setApiKeyStatus("ACTIVE");
        company.setApiKeyIssuedAt(LocalDateTime.now());
        company.setApiKeyExpiresAt(LocalDateTime.now().plusYears(1));
        
        Company saved = companyRepository.save(company);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public CompanyDto toggleApiKeyStatus(Long companyId) {
        log.info("Toggling API Key status for company ID: {}", companyId);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));
        
        String currentStatus = company.getApiKeyStatus();
        if ("ACTIVE".equalsIgnoreCase(currentStatus)) {
            company.setApiKeyStatus("INACTIVE");
        } else {
            company.setApiKeyStatus("ACTIVE");
        }
        
        Company saved = companyRepository.save(company);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MilestoneDto> getMilestonesByCompanyId(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));
        if (company.getMilestones() == null || company.getMilestones().isEmpty()) {
            return createDefaultMilestones(company).stream()
                    .map(m -> modelMapper.map(m, MilestoneDto.class))
                    .collect(Collectors.toList());
        }
        return company.getMilestones().stream()
                .map(m -> modelMapper.map(m, MilestoneDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MilestoneDto addMilestone(Long companyId, MilestoneDto milestoneDto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));
        int nextOrder = company.getMilestones() != null ? company.getMilestones().size() + 1 : 1;
        Milestone milestone = Milestone.builder()
                .title(milestoneDto.getTitle())
                .date(milestoneDto.getDate())
                .status(milestoneDto.getStatus() != null ? milestoneDto.getStatus() : "scheduled")
                .description(milestoneDto.getDescription())
                .orderIndex(milestoneDto.getOrderIndex() != null ? milestoneDto.getOrderIndex() : nextOrder)
                .company(company)
                .build();
        Milestone saved = milestoneRepository.save(milestone);
        return modelMapper.map(saved, MilestoneDto.class);
    }

    @Override
    @Transactional
    public MilestoneDto updateMilestone(Long companyId, Long milestoneId, MilestoneDto milestoneDto) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Milestone not found with ID: " + milestoneId));
        if (!milestone.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("Milestone does not belong to company ID: " + companyId);
        }
        milestone.setTitle(milestoneDto.getTitle());
        milestone.setDate(milestoneDto.getDate());
        milestone.setStatus(milestoneDto.getStatus());
        milestone.setDescription(milestoneDto.getDescription());
        if (milestoneDto.getOrderIndex() != null) {
            milestone.setOrderIndex(milestoneDto.getOrderIndex());
        }
        Milestone saved = milestoneRepository.save(milestone);
        return modelMapper.map(saved, MilestoneDto.class);
    }

    @Override
    @Transactional
    public void deleteMilestone(Long companyId, Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Milestone not found with ID: " + milestoneId));
        if (!milestone.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("Milestone does not belong to company ID: " + companyId);
        }
        milestoneRepository.delete(milestone);
    }

//helpers

    private List<Milestone> createDefaultMilestones(Company company) {
        List<Milestone> defaults = new ArrayList<>();
        defaults.add(Milestone.builder()
                .title("GRC Core Control Architecture Established")
                .date("Jan 15, 2026")
                .status("completed")
                .description("Defined and mapped base compliance standards covering initial security domains.")
                .orderIndex(1)
                .company(company)
                .build());
        defaults.add(Milestone.builder()
                .title("Real-time Telemetry Synchronization Enabled")
                .date("Mar 10, 2026")
                .status("completed")
                .description("Integrated Google Sheets automated catalog updates overriding static evidence.")
                .orderIndex(2)
                .company(company)
                .build());
        defaults.add(Milestone.builder()
                .title("External Attestation & Auditor Review")
                .date("May 04, 2026")
                .status("completed")
                .description("Independent third-party assessor verification completed with full attestation.")
                .orderIndex(3)
                .company(company)
                .build());
        defaults.add(Milestone.builder()
                .title("Continuous Monitoring & Live Trust State")
                .date("Jun 22, 2026")
                .status("active")
                .description("Active continuous posture state verified daily. Live security telemetry feeds.")
                .orderIndex(4)
                .company(company)
                .build());
        defaults.add(Milestone.builder()
                .title("Upcoming ISO 27001 Assessment Renewal")
                .date("Nov 12, 2026")
                .status("scheduled")
                .description("Scheduled re-evaluation of system networks and database partitions.")
                .orderIndex(5)
                .company(company)
                .build());
        return defaults;
    }

    private void updateCompanyMilestones(Company company, List<MilestoneDto> milestoneDtos) {
        List<Milestone> existingMilestones = company.getMilestones();
        List<Long> incomingIds = milestoneDtos.stream()
                .map(MilestoneDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        existingMilestones.removeIf(m -> m.getId() != null && !incomingIds.contains(m.getId()));

        int index = 1;
        for (MilestoneDto dto : milestoneDtos) {
            Optional<Milestone> existingOpt = Optional.empty();
            if (dto.getId() != null) {
                existingOpt = existingMilestones.stream()
                        .filter(m -> m.getId().equals(dto.getId()))
                        .findFirst();
            }
            if (existingOpt.isEmpty() && dto.getTitle() != null) {
                existingOpt = existingMilestones.stream()
                        .filter(m -> dto.getTitle().equalsIgnoreCase(m.getTitle()))
                        .findFirst();
            }

            if (existingOpt.isPresent()) {
                Milestone m = existingOpt.get();
                m.setTitle(dto.getTitle());
                m.setDate(dto.getDate());
                m.setStatus(dto.getStatus());
                m.setDescription(dto.getDescription());
                m.setOrderIndex(dto.getOrderIndex() != null ? dto.getOrderIndex() : index++);
            } else {
                Milestone newMilestone = Milestone.builder()
                        .title(dto.getTitle())
                        .date(dto.getDate())
                        .status(dto.getStatus() != null ? dto.getStatus() : "scheduled")
                        .description(dto.getDescription())
                        .orderIndex(dto.getOrderIndex() != null ? dto.getOrderIndex() : index++)
                        .build();
                company.addMilestone(newMilestone);
            }
        }
    }

    private CompanyDto mapToDto(Company company) {
        CompanyDto dto = modelMapper.map(company, CompanyDto.class);

        if(company.getDomains() != null && !company.getDomains().isEmpty()) {
            List<DomainDto> domainDto = company.getDomains().stream()
                    .map(this:: mapDomainToDto)
                    .collect(Collectors.toList());

            dto.setDomains(domainDto);
        }

        if (company.getMilestones() != null && !company.getMilestones().isEmpty()) {
            List<MilestoneDto> milestoneDtos = company.getMilestones().stream()
                    .map(m -> modelMapper.map(m, MilestoneDto.class))
                    .collect(Collectors.toList());
            dto.setMilestones(milestoneDtos);
        } else {
            List<Milestone> defaults = createDefaultMilestones(company);
            List<MilestoneDto> milestoneDtos = defaults.stream()
                    .map(m -> modelMapper.map(m, MilestoneDto.class))
                    .collect(Collectors.toList());
            dto.setMilestones(milestoneDtos);
        }

        return dto;
    }

    private DomainDto mapDomainToDto(Domain domain){
        DomainDto dto = modelMapper.map(domain, DomainDto.class);

        if(domain.getControls() !=null && !domain.getControls().isEmpty()) {
            List<ControlDto> controlDtos = domain.getControls().stream()
                    .map(control ->  modelMapper.map(control, ControlDto.class))
                    .collect(Collectors.toList());
            dto.setControls(controlDtos);
        }

        return dto;
    }

    private Company mapToEntity(CompanyDto dto){
        Company company = modelMapper.map(dto, Company.class);
        company.setId(null);

        if(dto.getDomains() != null) {
            company.getDomains().clear();
            for(DomainDto domainDto : dto.getDomains()) {
                Domain domain = mapDomainToEntity(domainDto);
                company.addDomain(domain);
            }
        }

        if (dto.getMilestones() != null && !dto.getMilestones().isEmpty()) {
            company.getMilestones().clear();
            for (MilestoneDto milestoneDto : dto.getMilestones()) {
                Milestone milestone = modelMapper.map(milestoneDto, Milestone.class);
                milestone.setId(null);
                company.addMilestone(milestone);
            }
        } else {
            List<Milestone> defaults = createDefaultMilestones(company);
            for (Milestone m : defaults) {
                company.addMilestone(m);
            }
        }

        return company;
    }

    private Domain mapDomainToEntity(DomainDto dto){
        Domain domain = modelMapper.map(dto, Domain.class);
        domain.setId(null);
        domain.setCompany(null);

        if(dto.getControls() != null) {
            domain.getControls().clear();
            for (ControlDto controlDto : dto.getControls()) {
                Control control = modelMapper.map(controlDto, Control.class);
                control.setId(null);
                domain.addControl(control);
            }
        }

        return domain;
    }
}
