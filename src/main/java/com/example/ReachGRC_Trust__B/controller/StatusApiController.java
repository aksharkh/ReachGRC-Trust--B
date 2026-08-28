package com.example.ReachGRC_Trust__B.controller;

import com.example.ReachGRC_Trust__B.dtos.StatusGroupDto;
import com.example.ReachGRC_Trust__B.dtos.StatusIncidentDto;
import com.example.ReachGRC_Trust__B.dtos.StatusServiceDto;
import com.example.ReachGRC_Trust__B.service.SystemStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StatusApiController {

    private final SystemStatusService statusService;

    @GetMapping
    public ResponseEntity<List<StatusGroupDto>> getStatus() {
        return ResponseEntity.ok(statusService.getAllStatusGroups());
    }

    @PutMapping("/service/{id}")
    public ResponseEntity<StatusServiceDto> updateService(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload
    ) {
        String status = payload.containsKey("status") ? (String) payload.get("status") : null;
        Double uptime = null;
        if (payload.containsKey("uptime")) {
            Object up = payload.get("uptime");
            if (up instanceof Number) {
                uptime = ((Number) up).doubleValue();
            }
        }
        return ResponseEntity.ok(statusService.updateService(id, status, uptime));
    }

    @PostMapping("/service/{id}/incident")
    public ResponseEntity<StatusIncidentDto> addIncident(
            @PathVariable Long id,
            @RequestBody StatusIncidentDto incidentDto
    ) {
        return ResponseEntity.ok(statusService.addIncident(id, incidentDto));
    }

    @PostMapping("/reset")
    public ResponseEntity<List<StatusGroupDto>> resetDefaults() {
        statusService.seedDefaultDataIfEmpty();
        return ResponseEntity.ok(statusService.getAllStatusGroups());
    }
}
