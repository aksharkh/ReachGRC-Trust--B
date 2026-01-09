package com.example.ReachGRC_Trust__B.dtos;


import com.example.ReachGRC_Trust__B.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
public class ControlDto {

    private Long id;

    @NotBlank(message = "Control name is required")
    private String name;

    @NotNull(message = "Control status is required")
    private Status status;

    private String remarks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
