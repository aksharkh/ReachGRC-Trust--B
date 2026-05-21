package com.example.ReachGRC_Trust__B.controller;

import com.example.ReachGRC_Trust__B.dtos.AdminUserDto.LoginDto.LoginRequest;
import com.example.ReachGRC_Trust__B.dtos.AdminUserDto.LoginDto.LoginResponseDto;
import com.example.ReachGRC_Trust__B.service.serviceImpl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trust/auth")
public class AuthController {

   private final AuthServiceImpl authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequest request) {

        log.info("REST: request to login by user with email: " + request.getEmail());

        return ResponseEntity.ok(authService.login(request));
    }

}
