package com.example.ReachGRC_Trust__B.service;

import com.example.ReachGRC_Trust__B.dtos.StatusGroupDto;
import com.example.ReachGRC_Trust__B.dtos.StatusIncidentDto;
import com.example.ReachGRC_Trust__B.dtos.StatusServiceDto;
import com.example.ReachGRC_Trust__B.entity.StatusIncident;
import com.example.ReachGRC_Trust__B.entity.SystemService;
import com.example.ReachGRC_Trust__B.entity.SystemStatusGroup;
import com.example.ReachGRC_Trust__B.repository.SystemServiceRepository;
import com.example.ReachGRC_Trust__B.repository.SystemStatusGroupRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemStatusService {

    private final SystemStatusGroupRepository groupRepository;
    private final SystemServiceRepository serviceRepository;

    @PostConstruct
    @Transactional
    public void init() {
        seedDefaultDataIfEmpty();
    }

    @Transactional(readOnly = true)
    public List<StatusGroupDto> getAllStatusGroups() {
        List<SystemStatusGroup> groups = groupRepository.findAllByOrderByOrderIndexAsc();
        if (groups.isEmpty()) {
            seedDefaultDataIfEmpty();
            groups = groupRepository.findAllByOrderByOrderIndexAsc();
        }

        return groups.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public StatusServiceDto updateService(Long serviceId, String status, Double uptime) {
        SystemService service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + serviceId));

        if (status != null) {
            service.setStatus(status.toLowerCase());
        }
        if (uptime != null) {
            service.setUptime(uptime);
        }

        SystemService saved = serviceRepository.save(service);
        return mapServiceToDto(saved);
    }

    @Transactional
    public StatusIncidentDto addIncident(Long serviceId, StatusIncidentDto incidentDto) {
        SystemService service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + serviceId));

        StatusIncident incident = new StatusIncident();
        incident.setDate(incidentDto.getDate());
        incident.setType(incidentDto.getType() != null ? incidentDto.getType() : "yellow");
        incident.setMsg(incidentDto.getMsg());

        service.addIncident(incident);
        serviceRepository.save(service);

        incidentDto.setId(incident.getId());
        return incidentDto;
    }

    @Transactional
    public void seedDefaultDataIfEmpty() {
        if (groupRepository.count() > 0) {
            return;
        }

        // 1. Core Platform Group
        SystemStatusGroup coreGroup = new SystemStatusGroup();
        coreGroup.setGroupName("Core Platform");
        coreGroup.setOrderIndex(1);

        SystemService dashboard = new SystemService(null, "Dashboard", "operational", 99.97, coreGroup, new ArrayList<>());
        dashboard.addIncident(new StatusIncident(null, "May 12, 2026", "yellow", "Elevated load times for ~18 min. Resolved.", dashboard));
        coreGroup.addService(dashboard);

        SystemService api = new SystemService(null, "Backend API", "operational", 99.91, coreGroup, new ArrayList<>());
        api.addIncident(new StatusIncident(null, "Apr 28, 2026", "yellow", "Intermittent 503 errors on /api/trust/public. Resolved.", api));
        api.addIncident(new StatusIncident(null, "Mar 7, 2026", "red", "Full outage for 22 min due to DB failover. Post-mortem published.", api));
        coreGroup.addService(api);

        SystemService auth = new SystemService(null, "Authentication", "operational", 99.99, coreGroup, new ArrayList<>());
        coreGroup.addService(auth);

        groupRepository.save(coreGroup);

        // 2. API Endpoints Group
        SystemStatusGroup apiGroup = new SystemStatusGroup();
        apiGroup.setGroupName("API Endpoints");
        apiGroup.setOrderIndex(2);

        SystemService publicApi = new SystemService(null, "Public API (/public/**)", "operational", 99.88, apiGroup, new ArrayList<>());
        publicApi.addIncident(new StatusIncident(null, "May 3, 2026", "yellow", "API key validation latency spike (~400ms avg). Resolved.", publicApi));
        publicApi.addIncident(new StatusIncident(null, "Apr 10, 2026", "yellow", "Elevated error rate on /image endpoints. Resolved.", publicApi));
        apiGroup.addService(publicApi);

        SystemService adminApi = new SystemService(null, "Admin API (/admin/**)", "operational", 99.95, apiGroup, new ArrayList<>());
        adminApi.addIncident(new StatusIncident(null, "Feb 20, 2026", "yellow", "Degraded performance for Excel import. Resolved.", adminApi));
        apiGroup.addService(adminApi);

        SystemService webhooks = new SystemService(null, "Webhook Delivery", "operational", 99.72, apiGroup, new ArrayList<>());
        webhooks.addIncident(new StatusIncident(null, "May 18, 2026", "yellow", "Delayed webhook delivery (~12 min lag). Resolved.", webhooks));
        webhooks.addIncident(new StatusIncident(null, "Apr 2, 2026", "red", "Webhook service down for 35 min. Resolved.", webhooks));
        apiGroup.addService(webhooks);

        groupRepository.save(apiGroup);

        // 3. Infrastructure Group
        SystemStatusGroup infraGroup = new SystemStatusGroup();
        infraGroup.setGroupName("Infrastructure");
        infraGroup.setOrderIndex(3);

        SystemService db = new SystemService(null, "Database", "operational", 99.96, infraGroup, new ArrayList<>());
        db.addIncident(new StatusIncident(null, "Mar 7, 2026", "red", "Primary DB failover caused 22 min downtime. Resolved.", db));
        infraGroup.addService(db);

        SystemService storage = new SystemService(null, "File Storage", "operational", 99.99, infraGroup, new ArrayList<>());
        infraGroup.addService(storage);

        SystemService auditLogs = new SystemService(null, "Audit Logs", "operational", 100.0, infraGroup, new ArrayList<>());
        infraGroup.addService(auditLogs);

        SystemService cdn = new SystemService(null, "CDN & Asset Delivery", "operational", 99.93, infraGroup, new ArrayList<>());
        cdn.addIncident(new StatusIncident(null, "May 1, 2026", "yellow", "Cache invalidation delay for logo assets. Resolved.", cdn));
        infraGroup.addService(cdn);

        groupRepository.save(infraGroup);
    }

    private StatusGroupDto mapToDto(SystemStatusGroup group) {
        StatusGroupDto dto = new StatusGroupDto();
        dto.setId(group.getId());
        dto.setGroup(group.getGroupName());
        dto.setItems(group.getServices().stream().map(this::mapServiceToDto).collect(Collectors.toList()));
        return dto;
    }

    private StatusServiceDto mapServiceToDto(SystemService service) {
        StatusServiceDto dto = new StatusServiceDto();
        dto.setId(service.getId());
        dto.setName(service.getName());
        dto.setStatus(service.getStatus());
        dto.setUptime(service.getUptime());
        dto.setIncidents(service.getIncidents().stream().map(inc -> new StatusIncidentDto(
                inc.getId(),
                inc.getDate(),
                inc.getType(),
                inc.getMsg()
        )).collect(Collectors.toList()));
        return dto;
    }
}
