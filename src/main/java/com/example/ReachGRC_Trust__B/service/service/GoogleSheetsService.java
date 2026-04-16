package com.example.ReachGRC_Trust__B.service.service;

import com.example.ReachGRC_Trust__B.dtos.CompanyDto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface GoogleSheetsService {

    List<CompanyDto> readSheetData(String spreadsheetId, String sheetTabName) throws IOException, GeneralSecurityException;
}
