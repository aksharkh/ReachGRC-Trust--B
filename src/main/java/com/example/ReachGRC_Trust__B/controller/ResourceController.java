package com.example.ReachGRC_Trust__B.controller;

import com.example.ReachGRC_Trust__B.dtos.resourceDtos.ResourceDto;
import com.example.ReachGRC_Trust__B.service.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/trust")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    // TODO: RESOURCES

    @GetMapping("{companyId}/resources/all")
    public ResponseEntity<?> listAllImages(@PathVariable Long companyId) {
        log.info("REST: request listing all resources for company Id: {}", companyId);

        return ResponseEntity
                .status(HttpStatus.OK)
//                .contentType(MediaType.valueOf("image/png"))
                .body(resourceService.listResources(companyId));
    }

    @PostMapping("/{companyId}/resource/new")
    public ResponseEntity<ResourceDto> uploadImage(@PathVariable Long companyId, @RequestParam("file") MultipartFile file) throws IOException {
        log.info("REST: request to upload file for company with Name: {}", companyId);

        return ResponseEntity.ok(resourceService.saveResource(companyId, file.getOriginalFilename(), file));

    }

    @PutMapping("/{companyId}/resource/{fileId}")
    public ResponseEntity<ResourceDto> updateImage(@PathVariable Long companyId, @PathVariable Long fileId, @RequestParam("file") MultipartFile file) throws IOException {
        log.info("REST: request for update file ID: {} for company Name: {}", fileId, companyId);

        return ResponseEntity.ok(resourceService.updateResource(companyId, fileId, file));
    }

    @DeleteMapping("/{companyId}/resource/{fileId}")
    public ResponseEntity<Void> deleteImageName(@PathVariable Long companyId, @PathVariable Long fileId) {
        log.info("REST: request for deleting File with ID: {}", fileId);

        resourceService.removeResource(companyId, fileId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{companyId}/resources/all")
    public ResponseEntity<Void> deleteAllImages(@PathVariable Long companyId) {
        log.info("REST: request for deleting all file for comapny Name: {}", companyId);

        resourceService.removeAllResources(companyId);
        return ResponseEntity.noContent().build();
    }

}
