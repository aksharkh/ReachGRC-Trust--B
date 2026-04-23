package com.example.ReachGRC_Trust__B.dtos.resourceDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfResponse {

    private Long companyId;
    private String companyName;
    private List<PdfDto> pdfs;
}
