package com.example.ReachGRC_Trust__B.service.serviceImpl;


import com.example.ReachGRC_Trust__B.config.ModelMapperConfig;
import com.example.ReachGRC_Trust__B.dtos.resourceDtos.ResourceDto;
import com.example.ReachGRC_Trust__B.dtos.resourceDtos.ResourceResponse;
import com.example.ReachGRC_Trust__B.entity.Company;
import com.example.ReachGRC_Trust__B.entity.Resource;
import com.example.ReachGRC_Trust__B.repository.CompanyRepository;
import com.example.ReachGRC_Trust__B.repository.ResourceRepository;

import com.example.ReachGRC_Trust__B.service.service.ResourceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
//@AllArgsConstructor
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final CompanyRepository companyRepository;
    private final ModelMapperConfig modelMapperConfig;

    @Transactional
    @Override
    public ResourceDto saveResource(Long companyId, String fileName, MultipartFile file) throws IOException {

        log.info("Fetching Company With Name: {}", companyId);
        Company company = companyRepository.findById(companyId).orElseThrow(() ->
             new RuntimeException("Company Not Found With Name...!")
        );

        log.info("Fetching File with Name: {}", fileName);
        if(resourceRepository.existsByFileName(fileName)) {
            throw new RuntimeException("File Already Exists");
        }

        byte[] fileData = file.getBytes();

        Resource savedResource = resourceRepository.save(
                Resource.builder()
                    .fileName(fileName)
                    .fileData(fileData)
                    .company(company)
                .build()
        );

        if(savedResource ==null) {
            throw new RuntimeException("Couldn't Save the file");
        }

        log.info("File Saved Successfully..!");

        return mapToDto(savedResource);
    }

    @Transactional
    @Override
    public ResourceDto updateResource(Long companyId, Long fileId, MultipartFile file) throws IOException {

        log.info("Fetching Company With Id: {}", companyId);
        Company company = companyRepository.findById(companyId).orElseThrow(() ->
                new RuntimeException("Company Not Found with Id: " + companyId)
        );

        log.info("Fetching File with ID: {}", fileId);
        if(!resourceRepository.existsByFileId(fileId)) {
            throw new RemoteException("File Not Found");
        }

        byte[] fileData = file.getBytes();

//        resourceDto.setUpdatedAt(LocalDateTime.now());
        Resource resource = resourceRepository.findByFileId(fileId);
        if(
                file!=null &&
                !Arrays.equals(fileData, resource.getFileData())) {

            resource.setFileData(fileData);
            resource.setUpdatedAt(LocalDateTime.now());
        }
//        resource.setCompany(company);
        Resource updatedResource = resourceRepository.save(resource);

        if(updatedResource == null) {
            throw new RuntimeException("Couldn't Update File with Name: " + updatedResource.getFileName());
        }
        return mapToDto(updatedResource);
    }

    @Transactional
    @Override
    public ResourceResponse listResources(Long companyId) {

        log.info("Fetching Company With Id: {}", companyId);
        Company company = companyRepository.findById(companyId).orElseThrow(()-> new RuntimeException("Company Not Found"));

        log.info("Fetching all the Files in Company: {}", companyId);
        List<ResourceDto> resources = mapToDtoList(resourceRepository.findByCompanyId(companyId));

        return new ResourceResponse(company.getId(), company.getCompanyName(), resources);
    }


    @Transactional
    @Override
    public void removeResource(Long companyId, Long fileId) {

        log.info("Fetching Company with Id: {}", companyId);
        if(!companyRepository.existsById(companyId)) {
            throw new RuntimeException("Company Not Found");
        }

        log.info("Fetching File with ID: {}", fileId);
        if(!resourceRepository.existsByFileId(fileId)) {
            throw new RuntimeException("File Not Found...!");
        }

        resourceRepository.deleteByFileId(fileId);
        log.info("File '{}' deleted successfully", fileId);
    }

    @Transactional
    @Override
    public void removeAllResources(Long companyId) {

        log.info("Fetching Company Id: {}", companyId);
        if(!companyRepository.existsById(companyId)) {
            throw new RuntimeException("Company NOt Found");
        }

        log.warn("Deleting all the resources from Company with Id: {}", companyId);

        resourceRepository.deleteByCompanyId(companyId);
//        log.warn("Deleted All the Files from Company with Id: {}", companyId;
    }


    //mappers
    private ResourceDto mapToDto(Resource resource) {
        return modelMapperConfig.modelMapper().map(resource, ResourceDto.class);
    }

    private Resource mapToEntity(ResourceDto resourceDto) {
        return modelMapperConfig.modelMapper().map(resourceDto, Resource.class);
    }

    private List<ResourceDto> mapToDtoList(List<Resource> resources) {
        return resources.stream().map(
                resource -> modelMapperConfig.modelMapper().map(resource, ResourceDto.class)
        ).toList();
    }
}

