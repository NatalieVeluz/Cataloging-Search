package com.catalog.dto;

/**
 * LoginRequest
 *
 * This Data Transfer Object (DTO) represents
 * the login credentials submitted by a user.
 *
 * It is used by the authentication controller
 * to receive login data from the frontend.
 *
 * The fields are automatically mapped from JSON
 * using the Jackson library.
 */
public class LoginRequest {

    /**
     * User's registered email address.
     */
    private String email;

    /**
     * User's account password.
     */
    private String password;

    /**
     * Returns the user's email.
     *
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     * Required for JSON deserialization by Jackson.
     *
     * @param email user email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the user's password.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     * Required for JSON deserialization by Jackson.
     *
     * @param password user password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}