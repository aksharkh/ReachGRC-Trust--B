package com.example.ReachGRC_Trust__B.controller;

import com.example.ReachGRC_Trust__B.service.service.LogoService;
import com.example.ReachGRC_Trust__B.service.service.PdfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.zip.DataFormatException;

@Slf4j
@RestController
@RequestMapping("/api/trust")
@RequiredArgsConstructor
public class ResourceController {

    private final LogoService logoService;
    private final PdfService pdfService;


    @GetMapping("/{companyId}/image/all")
    public ResponseEntity<?> getAllLogos(@PathVariable Long companyId) {
        log.info("REST: request to list all logos for company ID: {}", companyId);
        return ResponseEntity.status(HttpStatus.OK).body(logoService.getAllLogos(companyId));
    }

    @GetMapping("{companyId}/image/{logoId}")
    public ResponseEntity<?> getLogoById(@PathVariable Long companyId, @PathVariable Long logoId) throws DataFormatException, IOException {
        log.info("REST: request to fetch Logo with Id" + logoId + " from company with Id: "+ companyId);
        return ResponseEntity.status(HttpStatus.OK).body(logoService.getLogoById(companyId, logoId));
    }

    @PostMapping("/{companyId}/image/new")
    public ResponseEntity<?> uploadLogo(@PathVariable Long companyId,
                                        @RequestParam("file") MultipartFile file) throws IOException {
        log.info("REST: request to upload logo for company ID: {}", companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(logoService.uploadLogo(companyId, file));
    }

    @PutMapping("/{companyId}/image/{logoId}")
    public ResponseEntity<?> updateLogo(@PathVariable Long companyId,
                                        @PathVariable Long logoId,
                                        @RequestParam("file") MultipartFile file) throws IOException {
        log.info("REST: request to update logo ID: {} for company ID: {}", logoId, companyId);
        return ResponseEntity.ok(logoService.updateLogo(companyId, logoId, file));
    }

    @DeleteMapping("/{companyId}/image/{logoId}")
    public ResponseEntity<String> deleteLogo(@PathVariable Long companyId,
                                           @PathVariable Long logoId) {
        log.info("REST: request to delete logo ID: {} for company ID: {}", logoId, companyId);
        logoService.deleteLogo(companyId, logoId);
        return ResponseEntity.ok("Delete Company Logo with ID: "+ logoId + "From Company with ID: "+ companyId);
    }

    @DeleteMapping("/{companyId}/image/all")
    public ResponseEntity<String> deleteAllLogos(@PathVariable Long companyId) {
        log.info("REST: request to delete all logos for company ID: {}", companyId);
        logoService.deleteAllLogos(companyId);
        return ResponseEntity.ok("Deleted All Company Logos from Company with Id: "+ companyId);
    }


    @GetMapping("/{companyId}/pdf/all")
    public ResponseEntity<?> getAllPdfs(@PathVariable Long companyId) {
        log.info("REST: request to list all PDFs for company ID: {}", companyId);
        return ResponseEntity.status(HttpStatus.OK).body(pdfService.getAllPdfs(companyId));
    }

    @PostMapping("/{companyId}/pdf/new")
    public ResponseEntity<?> uploadPdf(@PathVariable Long companyId,
                                       @RequestParam("file") MultipartFile file) throws IOException {
        log.info("REST: request to upload PDF for company ID: {}", companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(pdfService.uploadPdf(companyId, file));
    }

    @PutMapping("/{companyId}/pdf/{pdfId}")
    public ResponseEntity<?> updatePdf(@PathVariable Long companyId,
                                       @PathVariable Long pdfId,
                                       @RequestParam("file") MultipartFile file) throws IOException {
        log.info("REST: request to update PDF ID: {} for company ID: {}", pdfId, companyId);
        return ResponseEntity.ok(pdfService.updatePdf(companyId, pdfId, file));
    }

    @DeleteMapping("/{companyId}/pdf/{pdfId}")
    public ResponseEntity<String> deletePdf(@PathVariable Long companyId,
                                          @PathVariable Long pdfId) {
        log.info("REST: request to delete PDF ID: {} for company ID: {}", pdfId, companyId);
        pdfService.deletePdf(companyId, pdfId);
        return ResponseEntity.ok("Deleted Pdf with Id " + pdfId+ " From company With Id" + companyId);
    }

    @DeleteMapping("/{companyId}/pdf/all")
    public ResponseEntity<String> deleteAllPdfs(@PathVariable Long companyId) {
        log.info("REST: request to delete all PDFs for company ID: {}", companyId);
        pdfService.deleteAllPdfs(companyId);
        return ResponseEntity.ok("Deleted All Pdf's from Company with Id: "+ companyId);
    }
}
