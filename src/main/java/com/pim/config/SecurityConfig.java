package com.pim.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${JWT_SECRET_KEY:defaultSuperSecretKeyAtLeast32CharactersLong}")
    private String jwtSecret;

    @Bean
    public JwtEncoder jwtEncoder() {
        System.out.println("🔑 JWT_SECRET_KEY (Base64 Encoded): " + jwtSecret);

        // Decode Base64 key
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        System.out.println("🛑 Decoded Key Bytes: " + Arrays.toString(keyBytes));

        if (keyBytes.length < 32) {
            throw new IllegalStateException("❌ JWT_SECRET_KEY must be at least 32 bytes long after decoding!");
        }

        // ✅ Assign a key ID
        String keyId = "my-key-id";

        // ✅ Build the JWK Key (WITHOUT `.keyType(KeyType.OCT)`)
        OctetSequenceKey jwk = new OctetSequenceKey.Builder(keyBytes)
                .keyID(keyId)
                .algorithm(JWSAlgorithm.HS256)  // ✅ Ensure correct algorithm
                .build();

        // ✅ Create JWK Set
        JWKSet jwkSet = new JWKSet(jwk);
        System.out.println("🔑 JWK Set (for signing): " + jwkSet.toJSONObject());

        // ✅ Ensure Nimbus selects the correct key
        JWKSource<SecurityContext> jwkSource = (selector, context) -> selector.select(jwkSet);

        // ✅ Debugging: Print selected key
        List<JWK> matchingKeys = jwkSet.getKeys();
        if (matchingKeys.isEmpty()) {
            throw new IllegalStateException("🚨 No matching keys found in JWK Set!");
        }
        System.out.println("✅ Nimbus JWT found signing key: " + matchingKeys.get(0).toJSONObject());

        return new NimbusJwtEncoder(jwkSource);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/token").permitAll()
                        .requestMatchers("/users/register").permitAll()
                        .requestMatchers("/api/products/read/**").hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers("/api/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder(JwtEncoder jwtEncoder) {
        return token -> {
            Jwt jwt = Jwt.withTokenValue(token)
                    .header("alg", "HS256")  // Adjust this to match your signing algorithm
                    .claims(claims -> claims.put("custom_claim", "value"))
                    .build();
            return jwt;
        };
    }
}
