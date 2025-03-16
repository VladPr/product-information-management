package com.pim.controller;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.pim.controller.UserController;
import com.pim.model.entity.User;
import com.pim.model.dto.UserRegistrationRequest;
import com.pim.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void testRegisterUser() {
        UserRegistrationRequest request = new UserRegistrationRequest("testUser", "password", "USER");

        when(userService.saveUser(anyString(), anyString(), anyString()))
                .thenReturn(new User(UUID.randomUUID(), "testuser", "hashedPassword", "USER"));

        ResponseEntity<String> response = userController.registerUser(request);
        assertThat(response.getBody()).isEqualTo("User registered successfully!");
    }

    @Test
    void testGetUser() {
        User user = new User(UUID.randomUUID(), "testUser", "password", "USER");
        when(userService.getUserByUsername("testUser")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUser("testUser");
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("testUser");
    }
}

