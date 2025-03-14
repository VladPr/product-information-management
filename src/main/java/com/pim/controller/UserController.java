package com.pim.controller;

import com.pim.model.entity.User;
import com.pim.model.dto.UserRegistrationRequest;
import com.pim.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request) { // ✅ Use DTO
        userService.saveUser(request.getUsername(), request.getPassword(), request.getRole());
        return ResponseEntity.ok("User registered successfully!");
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
