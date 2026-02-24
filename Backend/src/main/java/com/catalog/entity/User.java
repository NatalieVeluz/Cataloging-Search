package com.catalog.entity;

import com.catalog.enums.Role;
import jakarta.persistence.*;

/**
 * User Entity
 *
 * This entity represents a system user.
 * It stores authentication credentials and
 * role-based authorization information.
 *
 * The entity is mapped to the "users" table
 * in the database.
 *
 * This class supports the implementation of
 * Role-Based Access Control (RBAC).
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Primary key of the user.
     * Automatically generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    /**
     * Full name of the user.
     * Required field.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Unique email address used for login.
     * Must be unique in the database.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Encrypted password of the user.
     * Stored as a hashed value.
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Indicates whether the user account is active.
     */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Role assigned to the user.
     * Stored as a String representation of the enum.
     *
     * Example values:
     * - ADMIN
     * - STUDENT_ASSISTANT
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    // ================= GETTERS =================

    public Integer getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    /**
     * Returns the hashed password.
     * Should not be exposed in API responses.
     */
    public String getPassword() {
        return password;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public Role getRole() {
        return role;
    }

    // ================= SETTERS =================

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the hashed password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}