package com.example.ReachGRC_Trust__B.service.serviceImpl;

import com.example.ReachGRC_Trust__B.dtos.CompanyDto;
import com.example.ReachGRC_Trust__B.entity.SheetConfig;
import com.example.ReachGRC_Trust__B.repository.SheetConfigRepository;
import com.example.ReachGRC_Trust__B.service.service.CompanyService;
import com.example.ReachGRC_Trust__B.service.service.GoogleSheetsService;
import com.example.ReachGRC_Trust__B.service.service.SheetSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SheetSyncServiceImpl implements SheetSyncService {

    private final SheetConfigRepository sheetConfigRepository;
    private final GoogleSheetsService googleSheetsService;
    private final CompanyService companyService;

    @Override
    @Transactional
    public String syncSheetData() {
        log.info("Starting sheet data sync...");

        List<SheetConfig> configs = sheetConfigRepository.findAll();
        if (configs.isEmpty()) {
            return "No Sheet Configuration found";
        }

        SheetConfig config = configs.get(0); // Use the first one

        // Null-safe checks
        boolean isActive = Boolean.TRUE.equals(config.getIsActive());
        boolean isSyncEnabled = Boolean.TRUE.equals(config.getSyncEnabled());

        if (!isActive || !isSyncEnabled) {
            return "Sync is disabled for this configuration";
        }

        try {
            Long start = System.currentTimeMillis();
            List<CompanyDto> companiesParams = googleSheetsService.readSheetData(config.getSpreadsheetId(), config.getSheetTabName());

            if (companiesParams.isEmpty()) {
                updateConfigStatus(config, "SUCCESS - No Data Found", 0);
                return "Sync completed. No data found in sheet.";
            }

            List<CompanyDto> syncedCompanies = companyService.syncCompanies(companiesParams);

            updateConfigStatus(config, "SUCCESS", syncedCompanies.size());

            long duration = System.currentTimeMillis() - start;
            return "Sync completed successfully in " + duration + "ms. Synced " + syncedCompanies.size() + " companies.";

        } catch (Exception e) {
            log.error("Sync failed", e);
            updateConfigStatus(config, "FAILED: " + e.getMessage(), 0);
            throw new RuntimeException("Sync failed: " + e.getMessage(), e);
        }
    }

    private void updateConfigStatus(SheetConfig config, String status, int count) {
        config.setLastSyncStatus(status);
        config.setLastSyncTime(LocalDateTime.now());
        // Only update count if success, or keep previous? Logic implies strictly tracking this sync run.
        if (status.startsWith("SUCCESS")) {
            config.setCompaniesSynced(count);
        }
        sheetConfigRepository.save(config);
    }
}
