package com.example.ReachGRC_Trust__B.repository;


import com.example.ReachGRC_Trust__B.entity.SheetConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SheetConfigRepository extends JpaRepository<SheetConfig, Long> {

    Optional<SheetConfig> findBySheetName(String sheetName);

    boolean existsBySheetName(String sheetName);

    boolean existsBySpreadsheetId(String spreadsheetId);



}
