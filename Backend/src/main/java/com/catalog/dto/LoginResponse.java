package com.catalog.dto;

import com.catalog.enums.Role;

public class LoginResponse {

    private Integer userId;
    private String name;
    private String email;
    private Role role;
    private String message;

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

    public Integer getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }
}
