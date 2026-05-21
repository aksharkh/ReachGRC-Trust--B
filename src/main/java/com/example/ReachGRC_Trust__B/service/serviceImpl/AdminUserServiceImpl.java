package com.example.ReachGRC_Trust__B.service.serviceImpl;

import com.example.ReachGRC_Trust__B.config.ModelMapperConfig;
import com.example.ReachGRC_Trust__B.dtos.AdminUserDto.AdminRequestDto;
import com.example.ReachGRC_Trust__B.dtos.AdminUserDto.AdminResponseDto;
import com.example.ReachGRC_Trust__B.entity.AdminUser;
import com.example.ReachGRC_Trust__B.exceptions.DuplicateUserException;
import com.example.ReachGRC_Trust__B.repository.AdminUserRepository;
import com.example.ReachGRC_Trust__B.service.service.AdminUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository repository;
    private final ModelMapperConfig modelMapper;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    @Override
    public AdminResponseDto createAdminUser(AdminRequestDto user) {

        log.info("AdminUsrService: fetching user with email: " + user.getEmail());

        if(repository.existsByEmail(user.getEmail())) {
            throw new DuplicateUserException("User with email: " + user.getEmail() + " Already Exists");
        }
        AdminUser newUser = mapToEntity(user);


        // password encoding
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setIsActive(true);
        newUser.setRole("ADMIN");
        final AdminUser savedUser = repository.save(newUser);

        log.info("AdminUsrService: Account created for user with email: " + user.getEmail() + "Successfully");

        return mapToResponse(savedUser);
    }

    @Transactional
    @Override
    public AdminResponseDto updateAccountStatus(String email, Boolean status) {

        log.info("AdminUsrService: fetching user with email: " + email);

        AdminUser user = repository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not Found with Email: " + email));

        user.setIsActive(status);

        log.info("AdminUsrService: updating user status");

        return mapToResponse(repository.save(user));

    }

    @Transactional
    @Override
    public String deleteUser(String email) {
        if(!repository.existsByEmail(email)) {
            throw new UsernameNotFoundException("User not Found with Email: " + email);
        }

        repository.deleteByEmail(email);

        return "User with email: " + email + " deleted Successfully";
    }



    //mappers

    protected AdminUser mapToEntity(AdminRequestDto user) {
        return modelMapper.modelMapper().map(user, AdminUser.class);
    }

    protected AdminResponseDto mapToResponse(AdminUser user) {
        return modelMapper.modelMapper().map(user, AdminResponseDto.class);
    }
}
