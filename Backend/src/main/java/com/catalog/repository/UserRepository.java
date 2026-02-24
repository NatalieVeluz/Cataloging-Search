package com.catalog.repository;

// Import the User entity
import com.catalog.entity.User;

// Import JpaRepository to get built-in CRUD methods
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * UserRepository
 *
 * Handles all database operations related to system users
 * (Admin, Librarian, Student Assistant).
 *
 * By extending JpaRepository, it automatically provides:
 * - save()        -> create/update user
 * - findById()    -> get user by ID
 * - findAll()     -> retrieve all users
 * - deleteById()  -> delete user
 * - and other standard CRUD methods
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Finds a user by email address.
     *
     * This is commonly used for:
     * - Login authentication
     * - Checking if a user already exists
     * - Loading user details during security validation
     *
     * @param email the user's email (Mapúa email in your system)
     * @return Optional<User> to safely handle null results
     */
    Optional<User> findByEmail(String email);
}