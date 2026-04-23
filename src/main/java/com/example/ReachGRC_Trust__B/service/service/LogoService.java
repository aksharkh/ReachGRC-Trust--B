package com.example.ReachGRC_Trust__B.service.service;

import com.example.ReachGRC_Trust__B.dtos.resourceDtos.LogoDto;
import com.example.ReachGRC_Trust__B.dtos.resourceDtos.LogoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.zip.DataFormatException;

public interface LogoService {
    LogoDto uploadLogo(Long companyId, MultipartFile file) throws IOException;
    LogoDto updateLogo(Long companyId, Long logoId, MultipartFile file) throws IOException;
    LogoResponse getAllLogos(Long companyId);
    LogoDto getLogoById(Long companyId, Long logoId) throws DataFormatException, IOException;
    void deleteLogo(Long companyId, Long logoId);
    void deleteAllLogos(Long companyId);
}
