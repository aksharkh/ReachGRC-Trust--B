package com.example.ReachGRC_Trust__B.config;

import com.example.ReachGRC_Trust__B.entity.Company;
import com.example.ReachGRC_Trust__B.repository.CompanyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final CompanyRepository companyRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Only authenticate /api/trust/public/** endpoints, except health
        if (!path.startsWith("/api/trust/public/") || path.equals("/api/trust/public/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader("x-api-key");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "API key missing");
            return;
        }

        Optional<Company> companyOpt = companyRepository.findByApiKey(apiKey);

        if (companyOpt.isEmpty()) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Api key not found");
            return;
        }

        Company company = companyOpt.get();

        if (company.getApiKeyStatus() == null || !"ACTIVE".equalsIgnoreCase(company.getApiKeyStatus())) {
            writeErrorResponse(response, HttpStatus.FORBIDDEN, "Api Key is InActive");
            return;
        }

        if (company.getApiKeyExpiresAt() != null && company.getApiKeyExpiresAt().isBefore(LocalDateTime.now())) {
            writeErrorResponse(response, HttpStatus.FORBIDDEN, "Api Key is Expired");
            return;
        }

        // Set authenticated user context
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                company,
                apiKey,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String errorMessage) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        JSONObject json = new JSONObject();
        json.put("error", errorMessage);
        response.getWriter().write(json.toString());
    }
}
