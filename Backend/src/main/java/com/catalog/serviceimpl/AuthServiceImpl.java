package com.catalog.serviceimpl;

import com.catalog.dto.LoginRequest;
import com.catalog.dto.LoginResponse;
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

    @Override
    public LoginResponse login(LoginRequest request) {

        // 1️⃣ Validate email domain
        if (request.getEmail() == null ||
                !request.getEmail().endsWith("@mymail.mapua.edu.ph")) {
            throw new RuntimeException("Only Mapúa emails are allowed");
        }

        // 2️⃣ Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // 3️⃣ Check if account is active
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new RuntimeException("Account is disabled");
        }

        // 4️⃣ Plain text password check
        if (!request.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // 5️⃣ Track login
        LoginHistory history = new LoginHistory();
        history.setUser(user);
        loginHistoryRepository.save(history);

        // 6️⃣ Return success response
        return new LoginResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                "Login successful"
        );
    }
}
