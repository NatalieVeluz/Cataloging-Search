package com.catalog.controller;

import com.catalog.entity.User;
import com.catalog.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 *
 * This controller provides endpoints related to user data retrieval.
 * It interacts directly with the UserRepository to fetch user information
 * from the database.
 *
 * Base URL: /api/users
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserRepository userRepository;

    /**
     * Constructor-based dependency injection of UserRepository.
     *
     * @param userRepository repository responsible for database operations
     *                       related to User entities
     */
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * Endpoint: GET /api/users/{id}
     *
     * @param id unique identifier of the user
     * @return User entity if found
     * @throws RuntimeException if the user does not exist
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PutMapping("/{id}/password")
    public String updatePassword(
            @PathVariable Integer id,
            @RequestBody java.util.Map<String, String> body) {

        String newPassword = body.get("password");

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(newPassword); // plain text (for now)

        userRepository.save(user);

        return "Password updated successfully";
    }
}