package com.example.ReachGRC_Trust__B.service.serviceImpl;

import com.example.ReachGRC_Trust__B.config.CompressesAndDecompress;
import com.example.ReachGRC_Trust__B.config.ModelMapperConfig;
import com.example.ReachGRC_Trust__B.dtos.resourceDtos.LogoDto;
import com.example.ReachGRC_Trust__B.dtos.resourceDtos.LogoResponse;
import com.example.ReachGRC_Trust__B.entity.Company;
import com.example.ReachGRC_Trust__B.entity.Logo;
import com.example.ReachGRC_Trust__B.repository.CompanyRepository;
import com.example.ReachGRC_Trust__B.repository.LogoRepository;
import com.example.ReachGRC_Trust__B.service.service.LogoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoServiceImpl implements LogoService {

    private final LogoRepository logoRepository;
    private final CompanyRepository companyRepository;
    private final ModelMapperConfig modelMapperConfig;
    private final CompressesAndDecompress compressesAndDecompress;

    @Transactional
    @Override
    public LogoDto uploadLogo(Long companyId, MultipartFile file) throws IOException {

        log.info("Fetching Company with ID: {}", companyId);
        Company company = companyRepository.findById(companyId).orElseThrow(() ->
                new RuntimeException("Company Not Found with ID: " + companyId)
        );

        String fileName = file.getOriginalFilename();
        log.info("Uploading logo with name: {}", fileName);

        if (logoRepository.existsByFileName(fileName)) {
            throw new RuntimeException("Logo with name '" + fileName + "' already exists for this company");
        }

//        byte[] fileData = compressesAndDecompress.compress(file.getBytes());
        byte[] fileData = file.getBytes();
        Logo savedLogo = logoRepository.save(
                Logo.builder()
                        .fileName(fileName)
                        .fileData(fileData)
                        .company(company)
                        .build()
        );

        log.info("Logo saved successfully with ID: {}", savedLogo.getLogoId());
        return mapToDto(savedLogo);
    }

    @Transactional
    @Override
    public LogoDto updateLogo(Long companyId, Long logoId, MultipartFile file) throws IOException {

        log.info("Fetching Company with ID: {}", companyId);
        companyRepository.findById(companyId).orElseThrow(() ->
                new RuntimeException("Company Not Found with ID: " + companyId)
        );

        log.info("Fetching Logo with ID: {}", logoId);
        Logo logo = logoRepository.findByLogoId(logoId);
        if (logo == null) {
            throw new RuntimeException("Logo Not Found with ID: " + logoId);
        }

        byte[] newFileData = file.getBytes();
        if (file.getOriginalFilename() != null && !Arrays.equals(newFileData, logo.getFileData())) {
            logo.setFileName(file.getOriginalFilename());
            logo.setFileData(newFileData);
            logo.setUpdatedAt(LocalDateTime.now());
        }

        Logo updatedLogo = logoRepository.save(logo);
        log.info("Logo updated successfully with ID: {}", updatedLogo.getLogoId());
        return mapToDto(updatedLogo);
    }

    @Transactional
    @Override
    public LogoResponse getAllLogos(Long companyId) {

        log.info("Fetching Company with ID: {}", companyId);
        Company company = companyRepository.findById(companyId).orElseThrow(() ->
                new RuntimeException("Company Not Found with ID: " + companyId)
        );

        log.info("Fetching all logos for Company ID: {}", companyId);
        List<LogoDto> logos = logoRepository.findByCompanyId(companyId)
                .stream()
                .map(this::mapToDto)
                .toList();

        return new LogoResponse(company.getId(), company.getCompanyName(), logos);
    }

    @Override
    public LogoDto getLogoById(Long companyId,  Long logoId) throws DataFormatException, IOException {
        final Logo logo = logoRepository.findByLogoIdAndCompanyId(logoId, companyId);
        return new LogoDto(
            logo.getLogoId(),
            logo.getFileName(),
//            compressesAndDecompress.decompress(logo.getFileData())
            logo.getFileData(),
            logo.getCreatedAt(),
            logo.getUpdatedAt()
        );
    }


    @Transactional
    @Override
    public void deleteLogo(Long companyId, Long logoId) {

        log.info("Fetching Company with ID: {}", companyId);
        if (!companyRepository.existsById(companyId)) {
            throw new RuntimeException("Company Not Found with ID: " + companyId);
        }

        log.info("Deleting Logo with ID: {}", logoId);
        if (!logoRepository.existsByLogoId(logoId)) {
            throw new RuntimeException("Logo Not Found with ID: " + logoId);
        }

        logoRepository.deleteByLogoId(logoId);
        log.info("Logo '{}' deleted successfully", logoId);
    }

    @Transactional
    @Override
    public void deleteAllLogos(Long companyId) {

        log.info("Fetching Company with ID: {}", companyId);
        if (!companyRepository.existsById(companyId)) {
            throw new RuntimeException("Company Not Found with ID: " + companyId);
        }

        log.warn("Deleting all logos for Company ID: {}", companyId);
        logoRepository.deleteByCompanyId(companyId);
        log.warn("All logos deleted for Company ID: {}", companyId);
    }

    // ─── Mappers ─────────────────────────────────────────────────────────────

    private LogoDto mapToDto(Logo logo) {
        return modelMapperConfig.modelMapper().map(logo, LogoDto.class);
    }
}
