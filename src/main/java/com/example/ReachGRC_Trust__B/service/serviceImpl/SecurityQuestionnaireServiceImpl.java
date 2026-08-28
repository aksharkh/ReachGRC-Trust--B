package com.example.ReachGRC_Trust__B.service.serviceImpl;

import com.example.ReachGRC_Trust__B.dtos.SecurityQuestionnaireDto;
import com.example.ReachGRC_Trust__B.entity.SecurityQuestionnaire;
import com.example.ReachGRC_Trust__B.repository.SecurityQuestionnaireRepository;
import com.example.ReachGRC_Trust__B.service.service.SecurityQuestionnaireService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityQuestionnaireServiceImpl implements SecurityQuestionnaireService {

    private final SecurityQuestionnaireRepository repository;

    @Transactional
    @Override
    public SecurityQuestionnaireDto submitQuestionnaire(SecurityQuestionnaireDto dto) {
        log.info("Processing security questionnaire submission from: {}", dto.getEmail());

        String ticketId = dto.getTicketId();
        if (ticketId == null || ticketId.trim().isEmpty()) {
            ticketId = "SEC-2026-" + (int)(1000 + Math.random() * 9000);
        }

        SecurityQuestionnaire entity = SecurityQuestionnaire.builder()
                .ticketId(ticketId)
                .companyId(dto.getCompanyId())
                .companyName(dto.getCompanyName())
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .requesterCompany(dto.getRequesterCompany())
                .frameworkType(dto.getFrameworkType() != null ? dto.getFrameworkType() : "SIG_LITE")
                .targetDate(dto.getTargetDate())
                .notes(dto.getNotes())
                .attachedFileName(dto.getAttachedFileName())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SecurityQuestionnaire saved = repository.save(entity);
        log.info("Saved security questionnaire with ID: {} and Ticket: {}", saved.getId(), saved.getTicketId());

        return mapToDto(saved);
    }

    @Override
    public List<SecurityQuestionnaireDto> getQuestionnairesByCompanyId(Long companyId) {
        log.info("Fetching questionnaires for companyId: {}", companyId);
        return repository.findByCompanyIdOrderByCreatedAtDesc(companyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SecurityQuestionnaireDto> getAllQuestionnaires() {
        log.info("Fetching all security questionnaires");
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SecurityQuestionnaireDto getQuestionnaireById(Long id) {
        SecurityQuestionnaire entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Questionnaire request not found with id: " + id));
        return mapToDto(entity);
    }

    @Transactional
    @Override
    public SecurityQuestionnaireDto updateStatus(Long id, String status) {
        log.info("Updating status of questionnaire id: {} to {}", id, status);
        SecurityQuestionnaire entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Questionnaire request not found with id: " + id));
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());
        SecurityQuestionnaire updated = repository.save(entity);
        return mapToDto(updated);
    }

    @Transactional
    @Override
    public void deleteQuestionnaire(Long id) {
        log.info("Deleting questionnaire id: {}", id);
        repository.deleteById(id);
    }

    private SecurityQuestionnaireDto mapToDto(SecurityQuestionnaire entity) {
        return SecurityQuestionnaireDto.builder()
                .id(entity.getId())
                .ticketId(entity.getTicketId())
                .companyId(entity.getCompanyId())
                .companyName(entity.getCompanyName())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .requesterCompany(entity.getRequesterCompany())
                .frameworkType(entity.getFrameworkType())
                .targetDate(entity.getTargetDate())
                .notes(entity.getNotes())
                .attachedFileName(entity.getAttachedFileName())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
