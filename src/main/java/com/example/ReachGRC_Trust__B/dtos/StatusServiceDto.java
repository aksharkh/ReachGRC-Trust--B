package com.example.ReachGRC_Trust__B.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusServiceDto {
    private Long id;
    private String name;
    private String status; // operational, degraded, outage
    private Double uptime;
    private List<StatusIncidentDto> incidents = new ArrayList<>();
}
