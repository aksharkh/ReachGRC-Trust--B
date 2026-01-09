package com.example.ReachGRC_Trust__B.utils;


import com.example.ReachGRC_Trust__B.dtos.CompanyDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            Map<String, CompanyDto> companyMap = new LinkedHashMap<>();

            for(int i=0;i<= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
            }

        }
    }
}
