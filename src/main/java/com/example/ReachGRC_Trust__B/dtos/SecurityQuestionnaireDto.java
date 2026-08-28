package com.example.ReachGRC_Trust__B.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecurityQuestionnaireDto {

    private Long id;
    private String ticketId;
    private Long companyId;
    private String companyName;
    private String fullName;
    private String email;
    private String requesterCompany;
    private String frameworkType;
    private String targetDate;
    private String notes;
    private String attachedFileName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
