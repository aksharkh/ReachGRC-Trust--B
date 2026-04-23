package com.example.ReachGRC_Trust__B.service.service;

import com.example.ReachGRC_Trust__B.dtos.resourceDtos.PdfDto;
import com.example.ReachGRC_Trust__B.dtos.resourceDtos.PdfResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PdfService {
    PdfDto uploadPdf(Long companyId, MultipartFile file) throws IOException;
    PdfDto updatePdf(Long companyId, Long pdfId, MultipartFile file) throws IOException;
    PdfResponse getAllPdfs(Long companyId);
    void deletePdf(Long companyId, Long pdfId);
    void deleteAllPdfs(Long companyId);
}
