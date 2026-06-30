package com.example.ReachGRC_Trust__B.controller;

import com.example.ReachGRC_Trust__B.dtos.CompanyDto;
import com.example.ReachGRC_Trust__B.dtos.resourceDtos.PublicResourceResponseDto;
import com.example.ReachGRC_Trust__B.entity.Company;
import com.example.ReachGRC_Trust__B.entity.Resource;
import com.example.ReachGRC_Trust__B.repository.ResourceRepository;
import com.example.ReachGRC_Trust__B.service.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trust/public")
@RequiredArgsConstructor
@Slf4j
public class PublicApiController {

    private final CompanyService companyService;
    private final ResourceRepository resourceRepository;

    private Company getAuthenticatedCompany() {
        return (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("health");
    }

    @GetMapping("/me")
    public ResponseEntity<CompanyDto> getCompanyProfile() {
        Company company = getAuthenticatedCompany();
        CompanyDto dto = companyService.getCompanyById(company.getId());
        return ResponseEntity.ok(dto);
    }

    // ─── Image (Logo) Endpoints ──────────────────────────────────────────────

    @GetMapping("/image")
    public ResponseEntity<List<PublicResourceResponseDto>> listLogos() {
        Company company = getAuthenticatedCompany();
        List<Resource> resources = resourceRepository.findByCompanyId(company.getId());
        
        List<PublicResourceResponseDto> logos = resources.stream()
                .filter(r -> isImageFile(r.getFileName()))
                .map(this::mapToPublicResourceDto)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(logos);
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> uploadLogo(@RequestParam("file") MultipartFile file) throws IOException {
        Company company = getAuthenticatedCompany();
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        String fileName = file.getOriginalFilename();
        
        // Check for duplicates
        boolean exists = resourceRepository.findByCompanyId(company.getId()).stream()
                .anyMatch(r -> r.getFileName().equalsIgnoreCase(fileName));
                
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Logo already exists"));
        }

        Resource newResource = Resource.builder()
                .fileName(fileName)
                .fileData(file.getBytes())
                .company(company)
                .build();
                
        Resource saved = resourceRepository.save(newResource);
        return ResponseEntity.ok(mapToPublicResourceDto(saved));
    }

    @PutMapping(value = "/image/{logoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> updateLogo(@PathVariable Long logoId, @RequestParam("file") MultipartFile file) throws IOException {
        Company company = getAuthenticatedCompany();
        
        Resource existing = resourceRepository.findById(logoId)
                .orElse(null);
                
        if (existing == null || !existing.getCompany().getId().equals(company.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Logo not found"));
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        existing.setFileData(file.getBytes());
        Resource updated = resourceRepository.save(existing);
        return ResponseEntity.ok(mapToPublicResourceDto(updated));
    }

    @DeleteMapping("/image/{logoId}")
    @Transactional
    public ResponseEntity<?> deleteLogo(@PathVariable Long logoId) {
        Company company = getAuthenticatedCompany();
        
        Resource existing = resourceRepository.findById(logoId)
                .orElse(null);
                
        if (existing == null || !existing.getCompany().getId().equals(company.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Logo not found"));
        }

        resourceRepository.deleteByFileId(logoId);
        return ResponseEntity.ok("\"Deleted logo ID: " + logoId + " from company ID: " + company.getId() + "\"");
    }

    @DeleteMapping("/image/all")
    @Transactional
    public ResponseEntity<?> deleteAllLogos() {
        Company company = getAuthenticatedCompany();
        
        List<Resource> resources = resourceRepository.findByCompanyId(company.getId());
        List<Resource> logos = resources.stream()
                .filter(r -> isImageFile(r.getFileName()))
                .collect(Collectors.toList());
                
        for (Resource logo : logos) {
            resourceRepository.deleteByFileId(logo.getFileId());
        }
        
        return ResponseEntity.ok("\"Deleted all logos from company ID: " + company.getId() + "\"");
    }

    // ─── PDF Endpoints ────────────────────────────────────────────────────────

    @GetMapping("/pdf/all")
    public ResponseEntity<List<PublicResourceResponseDto>> listPdfs() {
        Company company = getAuthenticatedCompany();
        List<Resource> resources = resourceRepository.findByCompanyId(company.getId());
        
        List<PublicResourceResponseDto> pdfs = resources.stream()
                .filter(r -> isPdfFile(r.getFileName()))
                .map(this::mapToPublicResourceDto)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(pdfs);
    }

    @PostMapping(value = "/pdf/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> uploadPdf(@RequestParam("file") MultipartFile file) throws IOException {
        Company company = getAuthenticatedCompany();
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        String fileName = file.getOriginalFilename();
        
        // Check for duplicates
        boolean exists = resourceRepository.findByCompanyId(company.getId()).stream()
                .anyMatch(r -> r.getFileName().equalsIgnoreCase(fileName));
                
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Document already exists"));
        }

        Resource newResource = Resource.builder()
                .fileName(fileName)
                .fileData(file.getBytes())
                .company(company)
                .build();
                
        Resource saved = resourceRepository.save(newResource);
        return ResponseEntity.ok(mapToPublicResourceDto(saved));
    }

    @PutMapping(value = "/pdf/{pdfId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> updatePdf(@PathVariable Long pdfId, @RequestParam("file") MultipartFile file) throws IOException {
        Company company = getAuthenticatedCompany();
        
        Resource existing = resourceRepository.findById(pdfId)
                .orElse(null);
                
        if (existing == null || !existing.getCompany().getId().equals(company.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Document not found"));
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        existing.setFileData(file.getBytes());
        Resource updated = resourceRepository.save(existing);
        return ResponseEntity.ok(mapToPublicResourceDto(updated));
    }

    @DeleteMapping("/pdf/{pdfId}")
    @Transactional
    public ResponseEntity<?> deletePdf(@PathVariable Long pdfId) {
        Company company = getAuthenticatedCompany();
        
        Resource existing = resourceRepository.findById(pdfId)
                .orElse(null);
                
        if (existing == null || !existing.getCompany().getId().equals(company.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Document not found"));
        }

        resourceRepository.deleteByFileId(pdfId);
        return ResponseEntity.ok("\"Deleted document ID: " + pdfId + " from company ID: " + company.getId() + "\"");
    }

    @DeleteMapping("/pdf/all")
    @Transactional
    public ResponseEntity<?> deleteAllPdfs() {
        Company company = getAuthenticatedCompany();
        
        List<Resource> resources = resourceRepository.findByCompanyId(company.getId());
        List<Resource> pdfs = resources.stream()
                .filter(r -> isPdfFile(r.getFileName()))
                .collect(Collectors.toList());
                
        for (Resource pdf : pdfs) {
            resourceRepository.deleteByFileId(pdf.getFileId());
        }
        
        return ResponseEntity.ok("\"Deleted all PDFs from company ID: " + company.getId() + "\"");
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private boolean isImageFile(String fileName) {
        if (fileName == null) return false;
        String lower = fileName.toLowerCase();
        return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".gif");
    }

    private boolean isPdfFile(String fileName) {
        if (fileName == null) return false;
        String lower = fileName.toLowerCase();
        return lower.endsWith(".pdf");
    }

    private PublicResourceResponseDto mapToPublicResourceDto(Resource resource) {
        String name = resource.getFileName();
        String type = "application/octet-stream";
        if (name != null) {
            String lower = name.toLowerCase();
            if (lower.endsWith(".png")) type = "image/png";
            else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) type = "image/jpeg";
            else if (lower.endsWith(".pdf")) type = "application/pdf";
        }
        return PublicResourceResponseDto.builder()
                .id(resource.getFileId())
                .fileName(resource.getFileName())
                .fileType(type)
                .fileSize(resource.getFileData() != null ? (long) resource.getFileData().length : 0L)
                .uploadedAt(resource.getCreatedAt() != null ? resource.getCreatedAt() : resource.getUpdatedAt())
                .build();
    }
}
