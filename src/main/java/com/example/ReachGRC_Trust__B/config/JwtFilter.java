package com.example.ReachGRC_Trust__B.config;

import com.example.ReachGRC_Trust__B.entity.AdminUser;
import com.example.ReachGRC_Trust__B.repository.AdminUserRepository;
import com.example.ReachGRC_Trust__B.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AdminUserRepository adminUserRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // skip JWT filter for public (API key) routes and auth routes
        return path.startsWith("/api/trust/public/") ||
                path.startsWith("/api/trust/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authToken = request.getHeader("Authorization");

        if (authToken == null || !authToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authToken.substring(7);
        final String userEmail = jwtUtil.getUserName(token);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            AdminUser user = adminUserRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "User doesn't exist with email: " + userEmail));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(), null, user.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}