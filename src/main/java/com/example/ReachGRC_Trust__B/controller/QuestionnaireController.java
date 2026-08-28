package com.example.ReachGRC_Trust__B.controller;

import com.example.ReachGRC_Trust__B.dtos.SecurityQuestionnaireDto;
import com.example.ReachGRC_Trust__B.service.service.SecurityQuestionnaireService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trust/questionnaires")
@RequiredArgsConstructor
@Slf4j
public class QuestionnaireController {

    private final SecurityQuestionnaireService questionnaireService;

    /**
     * Public submission endpoint: stores the security questionnaire request in the database.
     */
    @PostMapping
    public ResponseEntity<SecurityQuestionnaireDto> submitQuestionnaire(@RequestBody SecurityQuestionnaireDto dto) {
        log.info("Received public security questionnaire request from: {}", dto.getEmail());
        SecurityQuestionnaireDto saved = questionnaireService.submitQuestionnaire(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * Get all submitted questionnaires for a specific company / organization.
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<SecurityQuestionnaireDto>> getQuestionnairesByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(questionnaireService.getQuestionnairesByCompanyId(companyId));
    }

    /**
     * Get all questionnaires across the platform.
     */
    @GetMapping
    public ResponseEntity<List<SecurityQuestionnaireDto>> getAllQuestionnaires() {
        return ResponseEntity.ok(questionnaireService.getAllQuestionnaires());
    }

    /**
     * Get single questionnaire by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SecurityQuestionnaireDto> getQuestionnaireById(@PathVariable Long id) {
        return ResponseEntity.ok(questionnaireService.getQuestionnaireById(id));
    }

    /**
     * Update status (e.g., PENDING -> IN_REVIEW -> COMPLETED).
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<SecurityQuestionnaireDto> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate
    ) {
        String newStatus = statusUpdate.get("status");
        if (newStatus == null || newStatus.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(questionnaireService.updateStatus(id, newStatus));
    }

    /**
     * Delete / archive questionnaire request.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestionnaire(@PathVariable Long id) {
        questionnaireService.deleteQuestionnaire(id);
        return ResponseEntity.noContent().build();
    }
}
