package com.example.ReachGRC_Trust__B.controller;


import com.example.ReachGRC_Trust__B.dtos.CompanyDto;
import com.example.ReachGRC_Trust__B.dtos.requestDtos.CompanyRequestDto;
import com.example.ReachGRC_Trust__B.service.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trust")
@RequiredArgsConstructor
@Slf4j
public class CompanyController {

    private final CompanyService companyService;

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


}
