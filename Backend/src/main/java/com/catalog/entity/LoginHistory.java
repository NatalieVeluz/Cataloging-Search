package com.catalog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * LoginHistory Entity
 *
 * This entity records user login activities in the system.
 * Each login attempt creates a new record linked to a specific user.
 *
 * The entity is mapped to the "login_history" table in the database.
 * It supports auditing and monitoring of authentication events.
 */
@Entity
@Table(name = "login_history")
public class LoginHistory {

    /**
     * Primary key of the login record.
     * Automatically generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_id")
    private Integer loginId;

    /**
     * Many-to-one relationship with the User entity.
     * Each login record belongs to one user.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Timestamp indicating when the login occurred.
     */
    @Column(name = "login_time")
    private LocalDateTime loginTime;

    /**
     * Automatically sets the login timestamp
     * before the entity is persisted to the database.
     */
    @PrePersist
    protected void onCreate() {
        loginTime = LocalDateTime.now();
    }

    // ================= GETTERS AND SETTERS =================

    public Integer getLoginId() {
        return loginId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }
}