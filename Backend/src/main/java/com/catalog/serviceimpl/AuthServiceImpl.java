package com.catalog.serviceimpl;

import com.catalog.dto.LoginRequest;
import com.catalog.dto.LoginResponse;
import com.catalog.entity.LoginHistory;
import com.catalog.entity.User;
import com.catalog.repository.LoginHistoryRepository;
import com.catalog.repository.UserRepository;
import com.catalog.service.AuthService;
import org.springframework.stereotype.Service;

/**
 * AuthServiceImpl
 *
 * Implements authentication logic for the system.
 *
 * Responsibilities:
 * - Validate Mapúa email domain
 * - Authenticate user credentials
 * - Check account status (active/inactive)
 * - Record login history
 * - Return login response with user details
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    /**
     * Constructor-based dependency injection.
     *
     * @param userRepository Repository for user data access
     * @param loginHistoryRepository Repository for login history tracking
     */
    public AuthServiceImpl(UserRepository userRepository,
                           LoginHistoryRepository loginHistoryRepository) {
        this.userRepository = userRepository;
        this.loginHistoryRepository = loginHistoryRepository;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        // Step 1: Validate Mapúa email domain
        if (request.getEmail() == null ||
                !request.getEmail().endsWith("@mymail.mapua.edu.ph")) {
            throw new RuntimeException("Only Mapúa emails are allowed");
        }

        // Step 2: Retrieve user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Step 3: Check if account is active
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new RuntimeException("Account is disabled");
        }

        // Step 4: Validate password (currently plain text comparison)
        if (!request.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Step 5: Record login activity in LoginHistory
        LoginHistory history = new LoginHistory();
        history.setUser(user);
        loginHistoryRepository.save(history);

        // Step 6: Return successful login response
        return new LoginResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                "Login successful"
        );
    }
}