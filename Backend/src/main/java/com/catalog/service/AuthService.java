package com.catalog.service;

// Import DTOs used for authentication
import com.catalog.dto.LoginRequest;
import com.catalog.dto.LoginResponse;

/**
 * AuthService
 *
 * This interface defines authentication-related operations
 * for the system.
 *
 * In your Cataloging Search Resources Platform,
 * this is responsible for handling user login logic.
 */
public interface AuthService {

    /**
     * Authenticates a user based on the provided login request.
     *
     * Expected flow:
     * 1. Validate email and password
     * 2. Check if user exists in the database
     * 3. Verify credentials
     * 4. Return login response (with role, token, or user details)
     *
     * @param request contains user login credentials (email, password)
     * @return LoginResponse containing authentication result
     */
    LoginResponse login(LoginRequest request);
}