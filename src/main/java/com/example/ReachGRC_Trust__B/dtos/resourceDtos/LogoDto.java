package com.example.ReachGRC_Trust__B.dtos.resourceDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogoDto {

    private Long logoId;
    private String fileName;
    private byte[] fileData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
