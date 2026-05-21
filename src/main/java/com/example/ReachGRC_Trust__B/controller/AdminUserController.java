package com.example.ReachGRC_Trust__B.controller;

import com.example.ReachGRC_Trust__B.dtos.AdminUserDto.AdminRequestDto;
import com.example.ReachGRC_Trust__B.dtos.AdminUserDto.AdminResponseDto;
import com.example.ReachGRC_Trust__B.service.serviceImpl.AdminUserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/trust/admin/user")
public class AdminUserController {

    private final AdminUserServiceImpl adminUserService;

    @PostMapping("/new")
    public ResponseEntity<AdminResponseDto> newUser(@RequestBody AdminRequestDto requestDto) {
        log.info("REST: request to create new admin account for user with email: " + requestDto.getEmail());

        return ResponseEntity.ok(adminUserService.createAdminUser(requestDto));
    }

    @PatchMapping("/{email}/{status}")
    public ResponseEntity<AdminResponseDto> updateStatus(@PathVariable String email, @PathVariable Boolean status) {
        log.info("REST: request to update account active status of user with email: " + email);

        return ResponseEntity.ok(adminUserService.updateAccountStatus(email, status));
    }

    @DeleteMapping("/{email}")
    ResponseEntity<String> deleteUser(@PathVariable String email) {
        log.warn("REST: request to delete user with email: " + email);

        return ResponseEntity.ok(adminUserService.deleteUser(email));
    }
}
