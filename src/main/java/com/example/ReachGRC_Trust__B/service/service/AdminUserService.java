package com.example.ReachGRC_Trust__B.service.service;

import com.example.ReachGRC_Trust__B.dtos.AdminUserDto.AdminRequestDto;
import com.example.ReachGRC_Trust__B.dtos.AdminUserDto.AdminResponseDto;

public interface AdminUserService {
    AdminResponseDto createAdminUser(AdminRequestDto user);
    AdminResponseDto updateAccountStatus(String email, Boolean status);

    String deleteUser(String email);
}
