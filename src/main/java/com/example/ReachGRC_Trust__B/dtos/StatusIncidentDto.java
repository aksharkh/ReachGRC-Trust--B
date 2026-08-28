package com.example.ReachGRC_Trust__B.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusIncidentDto {
    private Long id;
    private String date;
    private String type; // yellow, red
    private String msg;
}
