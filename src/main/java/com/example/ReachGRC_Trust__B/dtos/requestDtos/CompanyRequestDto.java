package com.example.ReachGRC_Trust__B.dtos.requestDtos;


import com.example.ReachGRC_Trust__B.dtos.DomainDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CompanyRequestDto {

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String statements;

    @Valid
    @NotBlank(message = "Company must have at least one domain")
    private List<DomainDto> domains = new ArrayList<>();

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
