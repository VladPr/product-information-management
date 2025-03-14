package com.pim.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    // Constructor-based injection for secret key
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);  // Decode Base64 key
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);     // Create HMAC key
    }

    // Generate JWT token
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", role);

        return Jwts.builder()
                .subject(username)                   // Set subject (username)
                .claims(claims)                      // Set claims (custom data)
                .issuedAt(new Date())                // Set issue time
                .expiration(new Date(System.currentTimeMillis() + 120 * 60 * 60 * 1000)) // 120 hours expiry
                .signWith(secretKey, Jwts.SIG.HS512) // Correct way to sign
                .compact();
    }

    // Extract claims from JWT
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)   // Verify signature
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Extract role from token
    public String extractRole(String token) {
        return (String) extractClaims(token).get("roles");
    }

    // Validate token
    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    // Check if token is expired
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
