package com.example.ReachGRC_Trust__B.dtos;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {

    private Long id;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String statement;

    @Valid
    @NotEmpty(message = "Company must have at least one domain")
    private List<DomainDto> domains = new ArrayList<>();

    private List<MilestoneDto> milestones = new ArrayList<>();

    private Boolean isActive;

    private Double latitude;
    private Double longitude;
    private String locationName;

    private String apiKey;
    private String apiKeyStatus;
    private LocalDateTime apiKeyIssuedAt;
    private LocalDateTime apiKeyExpiresAt;
    private String subscriptionPlan;
    private String subscriptionStatus;
    private LocalDateTime subscriptionExpiresAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
