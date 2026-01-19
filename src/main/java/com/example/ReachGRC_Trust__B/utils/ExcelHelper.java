package com.example.ReachGRC_Trust__B.utils;


import com.example.ReachGRC_Trust__B.dtos.CompanyDto;
import com.example.ReachGRC_Trust__B.dtos.ControlDto;
import com.example.ReachGRC_Trust__B.dtos.DomainDto;
import com.example.ReachGRC_Trust__B.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
@Slf4j
public class ExcelHelper {

    private static final String[] HEADERS ={
            "Company Name", "Statement", "Domain Name", "Control Name", "Control Status", "Remarks"
    };

    public List<CompanyDto> parseExcelFile(MultipartFile file) throws IOException {
        log.info("Parsing Excel file: {}", file.getOriginalFilename());

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)){

            Sheet sheet = workbook.getSheetAt(0);
            List<CompanyDto> companies = new ArrayList<>();

            Row headerRow = sheet.getRow(0);
            if(headerRow == null) {
                throw new IllegalArgumentException("Excel file is empty or has no header row");
            }

            List<ControlHeader> controlHeaders = parseHeaders(headerRow);
            log.info("Found {} control columns", controlHeaders.size());

            for(int i=1; i<= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if(row == null ||isRowEmpty(row)) {
                    continue;
                }

                CompanyDto company = parseCompanyRow(row, controlHeaders);
                if(company != null){
                    companies.add(company);
                }
            }

            log.info("Successfully parsed {} companies from Excel", companies.size());
            return companies;

        }


    }









    private List<ControlHeader> parseHeaders(Row headerRow) {
        List<ControlHeader> headers = new ArrayList<>();

        for(int i=2; i<headerRow.getLastCellNum(); i++ ) {
            Cell cell = headerRow.getCell(i);
            if(cell == null) continue;

            String headerText = getCellValueAsString(cell);
            if(headerText == null || headerText.trim().isEmpty()) continue;

            ControlHeader header = parseControlHeader(headerText);
            headers.add(header);
        }
        return  headers;
    }


    private ControlHeader parseControlHeader(String headerText) {
        headerText = headerText.trim();

        if(headerText.contains(":")){
            String[] parts = headerText.split(":", 2);
            return new ControlHeader(parts[0].trim(), parts[1].trim());
        } else {
            return new ControlHeader("General Security", headerText);
        }
    }



    private CompanyDto parseCompanyRow(Row row, List<ControlHeader> controlHeaders) {
        String companyName = getCellValueAsString(row.getCell(0));
        if(companyName == null || companyName.trim().isEmpty()) {
            return null;
        }


        String statement = getCellValueAsString(row.getCell(1));


        Map<String, List<ControlDto>> domainControlsMap = new LinkedHashMap<>();

        for( int i=0; i< controlHeaders.size();i++){
            ControlHeader header = controlHeaders.get(i);
            Cell statusCell = row.getCell(i+2);

            String statusStr = getCellValueAsString(statusCell);
            if(statusStr == null || statusStr.trim().isEmpty()) {
                continue;
            }

            Status status = parseControlStatus(statusStr);

            ControlDto control = ControlDto.builder()
                    .name(header.controlName)
                    .status(status)
                    .build();

            domainControlsMap.computeIfAbsent(header.domainName, k -> new ArrayList<>()).add(control);
        }

        List<DomainDto> domains = new ArrayList<>();
        for(Map.Entry<String, List<ControlDto>> entry : domainControlsMap.entrySet()) {
            if(!entry.getValue().isEmpty()) {
                DomainDto domain =  DomainDto.builder()
                        .name(entry.getKey())
                        .controls(entry.getValue())
                        .build();

                domains.add(domain);

            }
        }

        if(domains.isEmpty()) {
            log.info("No valid controls found for company: {}", companyName);
            return null;
        }

        return CompanyDto.builder()
                .companyName(companyName)
                .statement(statement)
                .domains(domains)
                .isActive(true)
                .build();

    }





    private String getCellValueAsString(Cell cell) {
        if(cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if(DateUtil.isCellDateFormatted(cell)){
                    yield cell.getDateCellValue().toString();

                } else {
                    double numValue = cell.getNumericCellValue();
                    if(numValue == Math.floor(numValue)) {
                        yield String.valueOf((long) numValue);

                    } else {
                        yield String.valueOf(numValue);
                    }
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> null;

        };
    }


    private boolean isRowEmpty(Row row){
        if(row == null) return true;

        Cell firstCell = row.getCell(0);
        if(firstCell !=null && firstCell.getCellType() != CellType.BLANK) {
            String value = getCellValueAsString(firstCell);

            if(value != null && !value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private Status parseControlStatus(String statusStr) {
        if(statusStr == null || statusStr.trim().isEmpty()) {
            return Status.PENDING;
        }

        return Status.valueOf(statusStr);
    }

    //helper
    private static class ControlHeader {
        String domainName;
        String controlName;

        ControlHeader(String domainName, String controlName) {
            this.domainName = domainName;
            this.controlName = controlName;
        }

        @Override
        public boolean equals(Object o){
            if(this == o) return true;

            if(o == null || getClass() != o.getClass()) return false;
            ControlHeader that = (ControlHeader) o;
            return Objects.equals(domainName, that.domainName) &&
                    Objects.equals(controlName, that.controlName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(domainName, controlName);
        }
    }
}
