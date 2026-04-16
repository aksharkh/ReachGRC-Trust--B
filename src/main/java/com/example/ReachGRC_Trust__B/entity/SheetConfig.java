package com.example.ReachGRC_Trust__B.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sheet_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SheetConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sheet_name", unique = true, nullable = false)
    private String sheetName;

    @Column(name = "sheet_url", columnDefinition = "TEXT", nullable = false)
    private String sheetUrl;

    @Column(name = "spreadsheet_id", nullable = false)
    private String spreadsheetId;

    @Column(name = "sheet_tab_name")
    @Builder.Default
    private String sheetTabName = "Sheet1";


    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;


    @Column(name = "sync_enabled")
    @Builder.Default
    private Boolean syncEnabled = true;

    @Column(name = "last_sync_time")
    private LocalDateTime lastSyncTime;

    @Column(name = "last_sync_status", columnDefinition = "TEXT")
    private String lastSyncStatus;

    @Column(name = "companies_synced")
    private Integer companiesSynced;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
