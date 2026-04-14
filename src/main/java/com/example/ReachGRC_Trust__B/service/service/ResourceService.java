package com.example.ReachGRC_Trust__B.service.service;

import com.example.ReachGRC_Trust__B.dtos.resourceDtos.ResourceDto;
import com.example.ReachGRC_Trust__B.dtos.resourceDtos.ResourceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResourceService {
    ResourceDto saveResource(String companyName, String fileName, MultipartFile file) throws IOException;
    ResourceDto updateResource(String companyName, Long fileId, MultipartFile file) throws IOException;

    ResourceResponse listResources(String companyName);

    void removeResource(String companyName, Long fileId);
    void removeAllResources(String companyName);
}
