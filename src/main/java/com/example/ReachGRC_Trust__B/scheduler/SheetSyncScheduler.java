package com.example.ReachGRC_Trust__B.scheduler;

import com.example.ReachGRC_Trust__B.service.service.SheetSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SheetSyncScheduler {

    private final SheetSyncService sheetSyncService;

    // Run every 5 minutes (300000 ms)
    @Scheduled(fixedRate = 60000)
    public void scheduleSheetSync() {
        log.info("Executing scheduled sheet sync...");
        try {
            String result = sheetSyncService.syncSheetData();
            log.info("Scheduled sync result: {}", result);
        } catch (Exception e) {
            log.error("Scheduled sync encountered an error", e);
        }
    }
}
