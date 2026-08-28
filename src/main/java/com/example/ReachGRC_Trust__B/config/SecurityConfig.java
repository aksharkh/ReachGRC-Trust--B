package com.example.ReachGRC_Trust__B.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApiKeyAuthFilter apiKeyAuthFilter;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){

        var cfg = new CorsConfiguration();

        cfg.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*", "http://192.168.1.*:*"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // 1. Authentication portal & health
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/trust/public/health").permitAll()
                        
                        // 2. Client synchronization endpoints (Accessible by CLIENT and ADMIN roles)
                        .requestMatchers("/api/trust/public/**").hasAnyRole("CLIENT", "ADMIN")
                        
                        // 3. Public read & submission endpoints (Permitted to anonymous users / ROLE_USER)
                        .requestMatchers(HttpMethod.GET, "/api/trust/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/trust/{companyId}/resource/{fileId}/verify-access").permitAll()
                        .requestMatchers("/api/trust/questionnaires/**").permitAll()
                        
                        // 4. Administrative modify endpoints (Strictly requires ADMIN role)
                        .requestMatchers("/api/subscription/downgrade").hasRole("ADMIN")
                        .requestMatchers("/api/sheet-config/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/trust/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/trust/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/trust/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/trust/**").hasRole("ADMIN")
                        
                        // 5. Fallback permissions
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                );

        return http.build();
    }
}
