package com.pim.controller;

import com.pim.model.dto.AuthRequest;
import com.pim.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/token")
    public Map<String, String> generateToken(@RequestBody AuthRequest authRequest) {
        return authService.authenticateAndGenerateToken(authRequest.getUsername(), authRequest.getPassword());
    }

}
