package com.example.ReachGRC_Trust__B.service.serviceImpl;

import com.example.ReachGRC_Trust__B.dtos.AdminUserDto.LoginDto.LoginRequest;
import com.example.ReachGRC_Trust__B.dtos.AdminUserDto.LoginDto.LoginResponseDto;
import com.example.ReachGRC_Trust__B.entity.AdminUser;
import com.example.ReachGRC_Trust__B.exceptions.InvalidCredentialsException;
import com.example.ReachGRC_Trust__B.repository.AdminUserRepository;
import com.example.ReachGRC_Trust__B.service.service.AuthServices;
import com.example.ReachGRC_Trust__B.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthServices {

    private final AdminUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponseDto login(LoginRequest request) {

        log.info("AuthService: fetching user with email: " + request.getEmail());

        // ✅ use findByEmail — returns full AdminUser entity
        AdminUser user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with Email: " + request.getEmail()));

        log.info("AuthService: Validating Credentials for user with email: " + request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }

        log.info("AuthService: Generating JWT Token for user with Email: " + request.getEmail());

        String token = jwtUtil.generateJwtToken(request.getEmail());

        return new LoginResponseDto(token, LocalDateTime.now());
    }


}
