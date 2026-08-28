package com.example.ReachGRC_Trust__B.controller;

import com.example.ReachGRC_Trust__B.config.JwtUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // Validate credentials
        if (("admin".equalsIgnoreCase(username) && "admin123".equals(password)) || 
            "admin123".equals(password)) {
            
            String token = jwtUtils.generateToken(username != null && !username.trim().isEmpty() ? username : "admin");
            
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", username != null && !username.trim().isEmpty() ? username : "admin");
            response.put("role", "ADMIN");
            
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid administrator credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
