package com.example.ReachGRC_Trust__B.service.serviceImpl;

import com.example.ReachGRC_Trust__B.config.ModelMapperConfig;
import com.example.ReachGRC_Trust__B.dtos.resourceDtos.PdfDto;
import com.example.ReachGRC_Trust__B.dtos.resourceDtos.PdfResponse;
import com.example.ReachGRC_Trust__B.entity.Company;
import com.example.ReachGRC_Trust__B.entity.Pdf;
import com.example.ReachGRC_Trust__B.repository.CompanyRepository;
import com.example.ReachGRC_Trust__B.repository.PdfRepository;
import com.example.ReachGRC_Trust__B.service.service.PdfService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final PdfRepository pdfRepository;
    private final CompanyRepository companyRepository;
    private final ModelMapperConfig modelMapperConfig;

    @Transactional
    @Override
    public PdfDto uploadPdf(Long companyId, MultipartFile file) throws IOException {

        log.info("Fetching Company with ID: {}", companyId);
        Company company = companyRepository.findById(companyId).orElseThrow(() ->
                new RuntimeException("Company Not Found with ID: " + companyId)
        );

        String fileName = file.getOriginalFilename();
        log.info("Uploading PDF with name: {}", fileName);

        if (pdfRepository.existsByFileName(fileName)) {
            throw new RuntimeException("PDF with name '" + fileName + "' already exists for this company");
        }

        byte[] fileData = file.getBytes();

        Pdf savedPdf = pdfRepository.save(
                Pdf.builder()
                        .fileName(fileName)
                        .fileData(fileData)
                        .company(company)
                        .build()
        );

        log.info("PDF saved successfully with ID: {}", savedPdf.getPdfId());
        return mapToDto(savedPdf);
    }

    @Transactional
    @Override
    public PdfDto updatePdf(Long companyId, Long pdfId, MultipartFile file) throws IOException {

        log.info("Fetching Company with ID: {}", companyId);
        companyRepository.findById(companyId).orElseThrow(() ->
                new RuntimeException("Company Not Found with ID: " + companyId)
        );

        log.info("Fetching PDF with ID: {}", pdfId);
        Pdf pdf = pdfRepository.findByPdfId(pdfId);
        if (pdf == null) {
            throw new RuntimeException("PDF Not Found with ID: " + pdfId);
        }

        byte[] newFileData = file.getBytes();
        if (file.getOriginalFilename() != null && !Arrays.equals(newFileData, pdf.getFileData())) {
            pdf.setFileName(file.getOriginalFilename());
            pdf.setFileData(newFileData);
            pdf.setUpdatedAt(LocalDateTime.now());
        }

        Pdf updatedPdf = pdfRepository.save(pdf);
        log.info("PDF updated successfully with ID: {}", updatedPdf.getPdfId());
        return mapToDto(updatedPdf);
    }

    @Transactional
    @Override
    public PdfResponse getAllPdfs(Long companyId) {

        log.info("Fetching Company with ID: {}", companyId);
        Company company = companyRepository.findById(companyId).orElseThrow(() ->
                new RuntimeException("Company Not Found with ID: " + companyId)
        );

        log.info("Fetching all PDFs for Company ID: {}", companyId);
        List<PdfDto> pdfs = pdfRepository.findByCompanyId(companyId)
                .stream()
                .map(this::mapToDto)
                .toList();

        return new PdfResponse(company.getId(), company.getCompanyName(), pdfs);
    }

    @Transactional
    @Override
    public void deletePdf(Long companyId, Long pdfId) {

        log.info("Fetching Company with ID: {}", companyId);
        if (!companyRepository.existsById(companyId)) {
            throw new RuntimeException("Company Not Found with ID: " + companyId);
        }

        log.info("Deleting PDF with ID: {}", pdfId);
        if (!pdfRepository.existsByPdfId(pdfId)) {
            throw new RuntimeException("PDF Not Found with ID: " + pdfId);
        }

        pdfRepository.deleteByPdfId(pdfId);
        log.info("PDF '{}' deleted successfully", pdfId);
    }

    @Transactional
    @Override
    public void deleteAllPdfs(Long companyId) {

        log.info("Fetching Company with ID: {}", companyId);
        if (!companyRepository.existsById(companyId)) {
            throw new RuntimeException("Company Not Found with ID: " + companyId);
        }

        log.warn("Deleting all PDFs for Company ID: {}", companyId);
        pdfRepository.deleteByCompanyId(companyId);
        log.warn("All PDFs deleted for Company ID: {}", companyId);
    }

    // ─── Mappers ─────────────────────────────────────────────────────────────

    private PdfDto mapToDto(Pdf pdf) {
        return modelMapperConfig.modelMapper().map(pdf, PdfDto.class);
    }
}
