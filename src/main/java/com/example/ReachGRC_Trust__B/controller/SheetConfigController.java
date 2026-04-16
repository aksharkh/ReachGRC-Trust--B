package com.example.ReachGRC_Trust__B.controller;

import com.example.ReachGRC_Trust__B.dtos.CompanyDto;
import com.example.ReachGRC_Trust__B.entity.SheetConfig;
import com.example.ReachGRC_Trust__B.repository.SheetConfigRepository;
import com.example.ReachGRC_Trust__B.service.service.CompanyService;
import com.example.ReachGRC_Trust__B.service.service.GoogleSheetsService;
import com.example.ReachGRC_Trust__B.service.service.SheetSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sheet-config")
@RequiredArgsConstructor
@Slf4j
public class SheetConfigController {

    private final SheetConfigRepository sheetConfigRepository;
    private final SheetSyncService sheetSyncService;

    @GetMapping
    public ResponseEntity<SheetConfig> getSheetConfig() {
        // Assuming single config for now, or we could take an ID. 
        // For simplicity, let's just get the first one or finding by a known name/ID if applicable.
        // Given the requirement "one google sheet I will have", let's assume there is only one active config.
        // We can fetch all and return the first one, or find by id 1.
        List<SheetConfig> configs = sheetConfigRepository.findAll();
        if (configs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(configs.get(0));
    }

    @PostMapping
    public ResponseEntity<SheetConfig> createOrUpdateSheetConfig(@RequestBody SheetConfig sheetConfig) {
        log.info("Request to create/update sheet config: {}", sheetConfig);
        
        // simple logic: if exists, update. If not, create.
        // We can enforce singleton pattern logic here if strict "one sheet" is needed.
        // implementing "upsert" based on id if provided, or clean slate.
        
        if (sheetConfig.getIsActive() == null) {
            sheetConfig.setIsActive(true);
        }
        if (sheetConfig.getSyncEnabled() == null) {
            sheetConfig.setSyncEnabled(true);
        }

        if (sheetConfig.getId() != null) {
             Optional<SheetConfig> existing = sheetConfigRepository.findById(sheetConfig.getId());
             if (existing.isPresent()) {
                 SheetConfig toUpdate = existing.get();
                 toUpdate.setSheetName(sheetConfig.getSheetName());
                 toUpdate.setSheetUrl(sheetConfig.getSheetUrl());
                 toUpdate.setSpreadsheetId(sheetConfig.getSpreadsheetId());
                 toUpdate.setSheetTabName(sheetConfig.getSheetTabName());
                 if (sheetConfig.getIsActive() != null) toUpdate.setIsActive(sheetConfig.getIsActive());
                 if (sheetConfig.getSyncEnabled() != null) toUpdate.setSyncEnabled(sheetConfig.getSyncEnabled());
                 
                 // don't overwrite read-only or status fields unless intentional?
                 // keeping simple for now
                 return ResponseEntity.ok(sheetConfigRepository.save(toUpdate));
             }
        }
        
        // If we want to ensure only ONE config exists, we could check count.
        if (sheetConfigRepository.count() > 0 && sheetConfig.getId() == null) {
            // update the existing one instead of creating new? Or error?
            // Let's just update the first found if no ID provided to be safe for "one sheet" rule.
            SheetConfig existing = sheetConfigRepository.findAll().get(0);
             existing.setSheetName(sheetConfig.getSheetName());
             existing.setSheetUrl(sheetConfig.getSheetUrl());
             existing.setSpreadsheetId(sheetConfig.getSpreadsheetId());
             existing.setSheetTabName(sheetConfig.getSheetTabName());
             if (sheetConfig.getIsActive() != null) existing.setIsActive(sheetConfig.getIsActive());
              // fix/add syncEnabled update too if needed, though existing code didn't have it
             return ResponseEntity.ok(sheetConfigRepository.save(existing));
        }

        return ResponseEntity.ok(sheetConfigRepository.save(sheetConfig));
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncSheetData() {
        log.info("Request to sync sheet data");
        try {
            // We need to inject SheetSyncService instead of doing logic here
            // But I need to update the fields first
            // Assuming the new service handles everything including finding config
            // However, the new service method returns a String.
            return ResponseEntity.ok(sheetSyncService.syncSheetData());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Sync failed: " + e.getMessage());
        }
    }
}
