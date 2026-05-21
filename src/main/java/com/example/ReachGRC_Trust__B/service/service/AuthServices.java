package com.example.ReachGRC_Trust__B.service.service;

import com.example.ReachGRC_Trust__B.dtos.AdminUserDto.LoginDto.LoginRequest;
import com.example.ReachGRC_Trust__B.dtos.AdminUserDto.LoginDto.LoginResponseDto;

public interface AuthServices {
    LoginResponseDto login(LoginRequest request);
}
