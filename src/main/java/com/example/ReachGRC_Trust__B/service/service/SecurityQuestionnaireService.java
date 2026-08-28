package com.example.ReachGRC_Trust__B.service.service;

import com.example.ReachGRC_Trust__B.dtos.SecurityQuestionnaireDto;

import java.util.List;

public interface SecurityQuestionnaireService {

    SecurityQuestionnaireDto submitQuestionnaire(SecurityQuestionnaireDto dto);

    List<SecurityQuestionnaireDto> getQuestionnairesByCompanyId(Long companyId);

    List<SecurityQuestionnaireDto> getAllQuestionnaires();

    SecurityQuestionnaireDto getQuestionnaireById(Long id);

    SecurityQuestionnaireDto updateStatus(Long id, String status);

    void deleteQuestionnaire(Long id);
}
