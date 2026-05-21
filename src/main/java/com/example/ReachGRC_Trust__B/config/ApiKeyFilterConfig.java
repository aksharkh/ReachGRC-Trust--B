package com.example.ReachGRC_Trust__B.config;

import com.example.ReachGRC_Trust__B.repository.CompanyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApiKeyFilterConfig extends OncePerRequestFilter {

    private final CompanyRepository companyRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/trust/public/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String apiKey = request.getHeader("x-api-key");
//
//        if (apiKey == null || !companyRepository.existsByApiKey(apiKey)) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return;
//        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "api-client", null,
                        List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
                )
        );

        filterChain.doFilter(request, response);
    }
}