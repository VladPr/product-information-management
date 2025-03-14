package com.pim.service;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    public Map<String, String> authenticateAndGenerateToken(String username, String password) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );


        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("http://localhost:9000")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(168*3600)) // Token expires 7 days
                .subject(username)
                .claim("roles", role)
                .build();

        try {
            JwtEncoderParameters params = JwtEncoderParameters.from(
                    JwsHeader.with(MacAlgorithm.HS256)
                            .keyId("my-key-id")
                            .build(),
                    claims
            );

            String jwtToken = jwtEncoder.encode(params).getTokenValue();

            Map<String, String> response = new HashMap<>();
            response.put("access_token", jwtToken);
            response.put("token_type", "Bearer");
            response.put("role", role);
            return response;
        } catch (Exception e) {
            System.out.println(" JWT Encoding Failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
