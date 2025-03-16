package com.pim.tests;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.pim.controller.AuthController;
import com.pim.model.dto.AuthRequest;
import com.pim.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testGenerateToken() {
        AuthRequest authRequest = new AuthRequest("user", "pass");
        when(authService.authenticateAndGenerateToken("user", "pass")).thenReturn(Map.of("token", "fake-jwt-token"));

        Map<String, String> response = authController.generateToken(authRequest);
        assertThat(response).containsEntry("token", "fake-jwt-token");
    }
}
