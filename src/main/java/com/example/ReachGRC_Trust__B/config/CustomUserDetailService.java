package com.example.ReachGRC_Trust__B.config;

import com.example.ReachGRC_Trust__B.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class CustomUserDetailService implements UserDetailsService {

    private final AdminUserRepository adminRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return adminRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User with email" + username + " not found"));
    }
}
