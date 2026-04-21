package com.example.ReachGRC_Trust__B.service.service;

import com.example.ReachGRC_Trust__B.dtos.resourceDtos.ResourceDto;
import com.example.ReachGRC_Trust__B.dtos.resourceDtos.ResourceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResourceService {
    ResourceDto saveResource(Long companyId, String fileName, MultipartFile file) throws IOException;
    ResourceDto updateResource(Long companyId, Long fileId, MultipartFile file) throws IOException;

    ResourceResponse listResources(Long companyId);

    void removeResource(Long companyId, Long fileId);
    void removeAllResources(Long companyId);
}
