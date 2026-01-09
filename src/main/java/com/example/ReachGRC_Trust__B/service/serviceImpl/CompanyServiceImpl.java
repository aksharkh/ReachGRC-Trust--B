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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;


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
