package com.catalog.entity;

import com.catalog.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    // ✅ NEW NAME FIELD
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "is_active")
    private Boolean isActive;

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

    public void setName(String name) {     // ✅ NEW
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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
