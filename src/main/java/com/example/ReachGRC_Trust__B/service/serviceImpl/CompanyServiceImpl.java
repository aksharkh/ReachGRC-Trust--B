package com.example.ReachGRC_Trust__B.service.serviceImpl;

import com.example.ReachGRC_Trust__B.dtos.CompanyDto;
import com.example.ReachGRC_Trust__B.dtos.ControlDto;
import com.example.ReachGRC_Trust__B.dtos.DomainDto;
import com.example.ReachGRC_Trust__B.dtos.requestDtos.CompanyRequestDto;
import com.example.ReachGRC_Trust__B.entity.Company;
import com.example.ReachGRC_Trust__B.entity.Control;
import com.example.ReachGRC_Trust__B.entity.Domain;
import com.example.ReachGRC_Trust__B.exceptions.DuplicateResourceException;
import com.example.ReachGRC_Trust__B.repository.CompanyRepository;
import com.example.ReachGRC_Trust__B.service.service.CompanyService;
import com.example.ReachGRC_Trust__B.utils.ExcelHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            // This case might be tricky if we match by name in syncCompanies
            // But if called directly via API, we might want to allow name change if ID matches
            // However, existing logic threw exception if name mismatched, which seems to imply
            // name is unique/immutable identity for this update context or it was a check?
            // "Company with name ... does not exists" message suggests it was checking if the new name is valid?
            // Actually, usually update shouldn't enforce name match with DTO if we are changing name.
            // But let's stick to existing logic for now unless it breaks sync.
            // In syncCompanies, we findByName, so name will always match.
             // existing checks:
             // if(!existingCompany.getCompanyName().equals(companyDto.getCompanyName()) ) {
             //    throw new DuplicateResourceException("Company with name "+ companyDto.getCompanyName()+ "does not exists");
             // }
        }

        // Update basic fields
        existingCompany.setStatement(companyDto.getStatement());

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

        Company updatedCompany = companyRepository.save(existingCompany);
        log.info("Company updated successfully with ID: {}", updatedCompany.getId());

        return mapToDto(updatedCompany);
    }

    private void updateDomains(Company company, List<DomainDto> domainDtos) {
        List<Domain> existingDomains = company.getDomains();
        
        // 1. Identify domains to remove (present in DB but not in DTO)
        List<String> incomingNames = domainDtos.stream()
                .map(DomainDto::getName)
                .collect(Collectors.toList());
        
        existingDomains.removeIf(domain -> !incomingNames.contains(domain.getName()));

        // 2. Identify domains to add or update
        for (DomainDto domainDto : domainDtos) {
            Optional<Domain> existingDomainOpt = existingDomains.stream()
                    .filter(d -> d.getName().equals(domainDto.getName()))
                    .findFirst();

            if (existingDomainOpt.isPresent()) {
                // Update existing domain
                Domain domain = existingDomainOpt.get();
                // update other fields if Domain has them (currently only name, which matches)
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
        List<String> incomingNames = controlDtos.stream()
                .map(ControlDto::getName)
                .collect(Collectors.toList());

        existingControls.removeIf(control -> !incomingNames.contains(control.getName()));

        // 2. Add or Update
        for (ControlDto controlDto : controlDtos) {
            Optional<Control> existingControlOpt = existingControls.stream()
                    .filter(c -> c.getName().equals(controlDto.getName()))
                    .findFirst();

            if (existingControlOpt.isPresent()) {
                Control control = existingControlOpt.get();
                // Update fields
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
                    CompanyDto updatedCompany = updateCompany(existingCompany.get().getId(), companyDto);
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








//helpers

    private CompanyDto mapToDto(Company company) {
        CompanyDto dto =modelMapper.map(company, CompanyDto.class);

        if(company.getDomains() != null && !company.getDomains().isEmpty()) {
            List<DomainDto> domainDto = company.getDomains().stream()
                    .map(this:: mapDomainToDto)
                    .collect(Collectors.toList());

            dto.setDomains(domainDto);
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
        return  company;

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
