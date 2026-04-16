package com.example.ReachGRC_Trust__B.dtos.resourceDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ResourceDto {

    private String fileName;

    private byte[] fileData;

    private LocalDateTime createAt;

    private LocalDateTime updatedAt;
//
//    private Long companyId;
//
//    private String companyName;

}
