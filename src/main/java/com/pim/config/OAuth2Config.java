package com.pim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class OAuth2Config {

    @Bean
    public UserDetailsService users() {
        return new InMemoryUserDetailsManager(
                createUser("admin", "admin", "ADMIN"),
                createUser("customer", "customer", "CUSTOMER")
        );
    }

    private UserDetails createUser(String username, String rawPassword, String role) {
        return User.builder()
                .username(username)
                .password(passwordEncoder().encode(rawPassword))  // Encode password securely
                .roles(role)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Use BCrypt for secure hashing
    }
}
