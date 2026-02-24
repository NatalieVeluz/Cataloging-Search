package com.catalog.controller;

import com.catalog.dto.LoginRequest;
import com.catalog.dto.LoginResponse;
import com.catalog.service.AuthService;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 *
 * This controller handles authentication-related endpoints.
 * It allows users to log in to the system by validating
 * their credentials through the AuthService.
 *
 * Base URL: /api/auth
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    /**
     * Constructor-based dependency injection of AuthService.
     *
     * @param authService service responsible for authentication logic
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles user login requests.
     *
     * Endpoint: POST /api/auth/login
     *
     * This method:
     * - Accepts login credentials (email and password)
     * - Delegates authentication to AuthService
     * - Returns user role and relevant authentication response data
     *
     * @param request LoginRequest containing user credentials
     * @return LoginResponse containing authentication result
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}