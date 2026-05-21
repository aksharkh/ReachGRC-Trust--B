package com.example.ReachGRC_Trust__B.utils;

import com.example.ReachGRC_Trust__B.exceptions.InvalidJwtTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import java.util.Date;

@Component
public class JwtUtil {


    @Value("${jwt.secretKey}")
    private String secretKey;

    protected SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(String email) {
        return Jwts.builder()
                .subject(email)
                .claim("role", "ADMIN")
                .signWith(getSecretKey())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 50000))
                .compact();
    }

    public String getUserName(String token) {
        Claims clams =Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return clams.getSubject();
    }

    public Boolean verifyValidToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (InvalidJwtTokenException e) {
            throw new InvalidJwtTokenException("Invalid Jwt Token");
        }
    }
}
