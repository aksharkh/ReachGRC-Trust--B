package com.example.ReachGRC_Trust__B.service.service;

import com.example.ReachGRC_Trust__B.dtos.CompanyDto;
import com.example.ReachGRC_Trust__B.dtos.requestDtos.CompanyRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CompanyService {

    CompanyDto getCompanyById(Long id);
    CompanyDto createCompany(CompanyDto companyDto);
    CompanyDto updateCompany(Long id, CompanyDto companyDto);
    List<CompanyDto> getAllActiveCompanies();
    List<CompanyDto> getAllCompanies();
    void softDeleteCompany(Long id);
    void activateCompany(Long id);

    List<CompanyDto> importFromExcel(MultipartFile file) throws IOException;
    List<CompanyDto> syncFromExcel(MultipartFile file) throws IOException;
}
