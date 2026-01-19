package com.example.ReachGRC_Trust__B.dtos;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainDto {


    private Long id;

    @NotBlank(message = "Domain name is required")
    private String name;

    @Valid
    @NotEmpty(message = "Domain must have at least one control")
    private List<ControlDto> controls = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
