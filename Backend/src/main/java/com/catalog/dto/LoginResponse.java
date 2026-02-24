package com.catalog.dto;

import com.catalog.enums.Role;

/**
 * LoginResponse
 *
 * This Data Transfer Object (DTO) represents
 * the response returned after a successful
 * authentication request.
 *
 * It contains user identity information and
 * role details required by the frontend to
 * implement role-based access control (RBAC).
 */
public class LoginResponse {

    /**
     * Unique identifier of the authenticated user.
     */
    private Integer userId;

    /**
     * Full name of the user.
     */
    private String name;

    /**
     * Registered email address of the user.
     */
    private String email;

    /**
     * Role assigned to the user.
     * Determines access permissions within the system.
     */
    private Role role;

    /**
     * Informational message returned after login.
     */
    private String message;

    /**
     * Parameterized constructor used to initialize
     * all response fields after successful authentication.
     *
     * @param userId  unique user ID
     * @param name    user full name
     * @param email   user email
     * @param role    assigned system role
     * @param message login status message
     */
    public LoginResponse(Integer userId,
                         String name,
                         String email,
                         Role role,
                         String message) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.message = message;
    }

    /**
     * Returns the user's unique identifier.
     *
     * @return user ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * Returns the user's full name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the user's email address.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the user's system role.
     *
     * @return role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Returns the login status message.
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }
}