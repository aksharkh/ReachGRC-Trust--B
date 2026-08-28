package com.example.ReachGRC_Trust__B.controller;


import com.example.ReachGRC_Trust__B.dtos.CompanyDto;

import com.example.ReachGRC_Trust__B.service.service.CompanyService;
import com.example.ReachGRC_Trust__B.service.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/trust")
@RequiredArgsConstructor
@Slf4j
public class CompanyController {

    private final CompanyService companyService;
    private final ResourceService resourceService;


    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable Long companyId){
        return ResponseEntity.ok(companyService.getCompanyById(companyId));
    }
    @GetMapping("/health")
    public ResponseEntity<String> health(){
        return ResponseEntity.ok("health");
    }


    @PostMapping
    public ResponseEntity<CompanyDto> createCompany(@RequestBody CompanyDto companyDto){
        return new ResponseEntity<>(companyService.createCompany(companyDto), HttpStatus.CREATED);
    }

    @GetMapping("/allActive")
    public ResponseEntity<List<CompanyDto>> getAllActiveCompanies(){
        return  ResponseEntity.ok(companyService.getAllActiveCompanies());
    }
    @GetMapping("/allCompanies")
    public ResponseEntity<List<CompanyDto>> getAllCompanies(){
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<String> softDeleteCompany(@PathVariable Long id) {
        log.info("REST request to deactivate company with ID: {}", id);
        companyService.softDeleteCompany(id);
        return ResponseEntity.ok("Company deactivated successfully");
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<String> activateCompany(@PathVariable Long id) {
        log.info("REST request to activate company with ID: {}", id);
        companyService.activateCompany(id);
        return ResponseEntity.ok("Company activated successfully");
    }


    @PostMapping("/import")
    public ResponseEntity<List<CompanyDto>> importCompanies(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("REST request to import companies from Excel file: {}", file.getOriginalFilename());

        List<CompanyDto> importedCompanies = companyService.importFromExcel(file);
        return ResponseEntity.ok(importedCompanies);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyDto> updateCompany(@PathVariable Long id, @RequestBody CompanyDto companyDto){
        log.info("REST request to update company with ID: {}", id);

        CompanyDto updatedCompany = companyService.updateCompany(id, companyDto);
        return ResponseEntity.ok(updatedCompany);
    }

    @PostMapping("/{id}/api-key/generate")
    public ResponseEntity<CompanyDto> generateApiKey(@PathVariable Long id) {
        log.info("REST request to generate API key for company ID: {}", id);
        return ResponseEntity.ok(companyService.generateApiKey(id));
    }

    @PatchMapping("/{id}/api-key/status")
    public ResponseEntity<CompanyDto> toggleApiKeyStatus(@PathVariable Long id) {
        log.info("REST request to toggle API key status for company ID: {}", id);
        return ResponseEntity.ok(companyService.toggleApiKeyStatus(id));
    }

    @GetMapping("/{companyId}/milestones")
    public ResponseEntity<List<com.example.ReachGRC_Trust__B.dtos.MilestoneDto>> getMilestones(@PathVariable Long companyId) {
        log.info("REST request to fetch milestones for company ID: {}", companyId);
        return ResponseEntity.ok(companyService.getMilestonesByCompanyId(companyId));
    }

    @PostMapping("/{companyId}/milestones")
    public ResponseEntity<com.example.ReachGRC_Trust__B.dtos.MilestoneDto> addMilestone(
            @PathVariable Long companyId, 
            @RequestBody com.example.ReachGRC_Trust__B.dtos.MilestoneDto milestoneDto) {
        log.info("REST request to add milestone for company ID: {}", companyId);
        return new ResponseEntity<>(companyService.addMilestone(companyId, milestoneDto), HttpStatus.CREATED);
    }

    @PutMapping("/{companyId}/milestones/{milestoneId}")
    public ResponseEntity<com.example.ReachGRC_Trust__B.dtos.MilestoneDto> updateMilestone(
            @PathVariable Long companyId, 
            @PathVariable Long milestoneId, 
            @RequestBody com.example.ReachGRC_Trust__B.dtos.MilestoneDto milestoneDto) {
        log.info("REST request to update milestone ID: {} for company ID: {}", milestoneId, companyId);
        return ResponseEntity.ok(companyService.updateMilestone(companyId, milestoneId, milestoneDto));
    }

    @DeleteMapping("/{companyId}/milestones/{milestoneId}")
    public ResponseEntity<String> deleteMilestone(
            @PathVariable Long companyId, 
            @PathVariable Long milestoneId) {
        log.info("REST request to delete milestone ID: {} for company ID: {}", milestoneId, companyId);
        companyService.deleteMilestone(companyId, milestoneId);
        return ResponseEntity.ok("Milestone deleted successfully");
    }

}
