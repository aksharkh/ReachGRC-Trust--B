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

    @GetMapping("{companyName}/resources/all")
    public ResponseEntity<?> listAllImages(@PathVariable String companyName) {
        log.info("REST: request listing all resources for company Name: {}", companyName);

        return ResponseEntity
                .status(HttpStatus.OK)
//                .contentType(MediaType.valueOf("image/png"))
                .body(resourceService.listResources(companyName));
    }

    @PostMapping("/{companyName}/resource/")
    public ResponseEntity<ResourceDto> uploadImage(@PathVariable String companyName, @RequestParam("file") MultipartFile file) throws IOException {
        log.info("REST: request to upload file for company with Name: {}", companyName);

        return ResponseEntity.ok(resourceService.saveResource(companyName, file.getOriginalFilename(), file));

    }

    @PutMapping("/{companyName}/resource/{fileId}")
    public ResponseEntity<ResourceDto> updateImage(@PathVariable String companyName, @PathVariable Long fileId, @RequestParam("file") MultipartFile file) throws IOException {
        log.info("REST: request for update file ID: {} for company Name: {}", fileId, companyName);

        return ResponseEntity.ok(resourceService.updateResource(companyName, fileId, file));
    }

    @DeleteMapping("/{companyName}/resource/{fileId}")
    public ResponseEntity<Void> deleteImageName(@PathVariable String companyName, @PathVariable Long fileId) {
        log.info("REST: request for deleting File with ID: {}", fileId);

        resourceService.removeResource(companyName, fileId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{companyName}/all")
    public ResponseEntity<Void> deleteAllImages(@PathVariable String companyName) {
        log.info("REST: request for deleting all file for comapny Name: {}", companyName);

        resourceService.removeAllResources(companyName);
        return ResponseEntity.noContent().build();
    }

}
