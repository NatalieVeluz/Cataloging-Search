package com.catalog.controller;

import com.catalog.dto.LoginRequest;
import com.catalog.dto.LoginResponse;
import com.catalog.dto.RegisterRequest;
import com.catalog.dto.ResetPasswordRequest;
import com.catalog.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return "User registered successfully";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return "Password reset successful";
    }
}