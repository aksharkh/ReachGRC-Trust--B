package com.example.ReachGRC_Trust__B.service.serviceImpl;

import com.example.ReachGRC_Trust__B.dtos.CompanyDto;
import com.example.ReachGRC_Trust__B.dtos.ControlDto;
import com.example.ReachGRC_Trust__B.dtos.DomainDto;
import com.example.ReachGRC_Trust__B.enums.Status;
import com.example.ReachGRC_Trust__B.service.service.GoogleSheetsService;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleSheetsServiceImpl implements GoogleSheetsService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/spreadsheets.readonly");

    @Value("${google.sheets.credentials-path}")
    private String credentialsFilePath;

    @Value("${google.sheets.application-name}")
    private String applicationName;

    @Override
    public List<CompanyDto> readSheetData(String spreadsheetId, String sheetTabName) throws IOException, GeneralSecurityException {

        log.info("Reading data from the Google Sheet: {} (Tab: {})", spreadsheetId, sheetTabName);

        Sheets sheetsService = getSheetService();

        String range = sheetTabName + "!A:ZZ";
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> values = response.getValues();

        if(values == null || values.isEmpty()) {
            log.warn("No data found in sheet");
            return new ArrayList<>();
        }

        return parseSheetData(values);
    }

    public List<CompanyDto> parseSheetData(List<List<Object>> values) {

        if(values.size() < 2) {
            log.warn("Sheet has less than 2 rows (header + data)");
            return new ArrayList<>();
        }

        List<Object> headerRow = values.get(0);
        List<ControlHeader> controlHeaders = parseHeaders(headerRow);

        log.info("Found {} control columns", controlHeaders.size());

        List<CompanyDto> companies = new ArrayList<>();

        for(int i= 1; i<values.size(); i++) {
            List<Object> row = values.get(i);

            if(isRowEmpty(row)) {
                continue;
            }

            CompanyDto company = parseCompanyRow(row, controlHeaders);

            if(company != null){
                companies.add(company);
            }
        }

        log.info("Successfully parsed {} companies from google sheet", companies.size());
        return companies;
    }




    private List<ControlHeader> parseHeaders(List<Object> headerRow){
        List<ControlHeader> headers = new ArrayList<>();

        for(int i=2; i< headerRow.size(); i++) {
            String headerText = getCellValue(headerRow, i);

            if(headerText == null || headerText.trim().isEmpty()) {
                continue;
            }

            ControlHeader header = parseControlHeader(headerText);
            headers.add(header);
        }

        return headers;
    }


    private ControlHeader parseControlHeader(String headerText) {
        headerText = headerText.trim();

        if(headerText.contains(":")) {
            String[] parts = headerText.split(":", 2);
            return  new ControlHeader(parts[0].trim(), parts[1].trim());
        } else {
            return new ControlHeader("General Security", headerText);
        }
    }


    private CompanyDto parseCompanyRow(List<Object> row, List<ControlHeader> controlHeaders) {

        String companyName = getCellValue(row, 0);

        if(companyName == null || companyName.trim().isEmpty()) {
            return null;
        }

        String statement = getCellValue(row,1);


        Map<String, List<ControlDto>> domainCOntrolsMap = new LinkedHashMap<>();

        for(int i=0; i< controlHeaders.size(); i++) {
            ControlHeader header = controlHeaders.get(i);

            String statusStr = getCellValue(row, i+2);

            if(statusStr == null || statusStr.trim().isEmpty()){
                continue;
            }

            Status status = parseControlStatus(statusStr);

            ControlDto control = ControlDto.builder()
                    .name(header.controlName)
                    .status(status)
                    .build();

            domainCOntrolsMap
                    .computeIfAbsent(header.domainName, k -> new ArrayList<>())
                    .add(control);
        }


        List<DomainDto> domains = new ArrayList<>();
        for(Map.Entry<String, List<ControlDto>> entry : domainCOntrolsMap.entrySet()) {
            if(!entry.getValue().isEmpty()) {
                DomainDto domain = DomainDto.builder()
                        .name(entry.getKey())
                        .controls(entry.getValue())
                        .build();
                domains.add(domain);
            }
        }

        if(domains.isEmpty()){
            log.warn("No valid controls found for company: {}", companyName);
            return null;
        }

        return CompanyDto.builder()
                .companyName(companyName)
                .statement(statement)
                .domains(domains)
                .isActive(true)
                .build();

    }



    private String getCellValue(List<Object> row, int index){

        if(row == null || index >= row.size()){
            return null;
        }

        Object value = row.get(index);
        return value != null? value.toString().trim() : null;
    }


    private boolean isRowEmpty(List<Object> row) {
        if(row == null || row.isEmpty()) {
            return true;
        }

        String firstCell = getCellValue(row, 0);
        return firstCell == null || firstCell.isEmpty();
    }

    private Status parseControlStatus(String statusStr) {
        if(statusStr == null || statusStr.trim().isEmpty()) {
            return Status.PENDING;
        }

        return Status.valueOf(statusStr);
    }







    private Sheets getSheetService() throws IOException, GeneralSecurityException {

        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(credentialsFilePath))
                .createScoped(SCOPES);

        return new Sheets.Builder(httpTransport, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(applicationName)
                .build();
    }

    private String extractSpreadSheetId(String sheetUrl){

        try {
            String[] parts = sheetUrl.split("/");
            for(int i=0; i<parts.length; i++){
                if(parts[i].equals("d") && i+1< parts.length) {
                    return parts[i+1];
                }
            }
        } catch (Exception e) {
            log.error("Error extracting spreadsheet ID from the URL: {}",sheetUrl,e);
        }

        throw new IllegalArgumentException("Invalid google sheets url format");
    }

    private static class ControlHeader {
        String domainName;
        String controlName;

        ControlHeader(String domainName, String controlName) {
            this.domainName = domainName;
            this.controlName = controlName;
        }
    }
}
