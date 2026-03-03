package com.catalog.serviceimpl;

import com.catalog.dto.*;
import com.catalog.entity.LoginHistory;
import com.catalog.entity.User;
import com.catalog.repository.LoginHistoryRepository;
import com.catalog.repository.UserRepository;
import com.catalog.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    public AuthServiceImpl(UserRepository userRepository,
                           LoginHistoryRepository loginHistoryRepository) {
        this.userRepository = userRepository;
        this.loginHistoryRepository = loginHistoryRepository;
    }

    // ================= LOGIN =================

    @Override
    public LoginResponse login(LoginRequest request) {

        if (request.getEmail() == null ||
                !request.getEmail().endsWith("@mymail.mapua.edu.ph")) {
            throw new RuntimeException("Only Mapúa emails are allowed");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new RuntimeException("Account is disabled");
        }

        if (!request.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        LoginHistory history = new LoginHistory();
        history.setUser(user);
        loginHistoryRepository.save(history);

        return new LoginResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                "Login successful"
        );
    }

    // ================= REGISTER =================

    @Override
    public void register(RegisterRequest request) {

        if (request.getEmail() == null ||
                !request.getEmail().endsWith("@mymail.mapua.edu.ph")) {
            throw new RuntimeException("Only Mapúa emails are allowed");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // plain text
        user.setIsActive(true);

        if (request.getRole() == null) {
            user.setRole(com.catalog.enums.Role.STUDENT_ASSISTANT);
        } else {
            user.setRole(request.getRole());
        }

        userRepository.save(user);
    }

    // ================= RESET PASSWORD (SIMPLE VERSION) =================

    @Override
    public void resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
    }

    // ================= PASSWORD VALIDATION =================

    private void validatePassword(String password) {

        if (password == null) {
            throw new RuntimeException("Password cannot be null");
        }

        // Minimum length 12
        if (password.length() < 12) {
            throw new RuntimeException("Password must be at least 12 characters long");
        }

        // Must contain uppercase
        if (!password.matches(".*[A-Z].*")) {
            throw new RuntimeException("Password must contain at least one uppercase letter");
        }

        // Must contain lowercase
        if (!password.matches(".*[a-z].*")) {
            throw new RuntimeException("Password must contain at least one lowercase letter");
        }

        // Must contain number
        if (!password.matches(".*[0-9].*")) {
            throw new RuntimeException("Password must contain at least one number");
        }

        // Must contain symbol
        if (!password.matches(".*[!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/].*")) {
            throw new RuntimeException("Password must contain at least one special character");
        }
    }
}