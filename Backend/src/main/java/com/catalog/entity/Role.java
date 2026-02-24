package com.catalog.entity;

import jakarta.persistence.*;

/**
 * Role Entity
 *
 * This entity represents a user role within the system.
 * Roles define access permissions and support the
 * implementation of Role-Based Access Control (RBAC).
 *
 * The entity is mapped to the "roles" table in the database.
 * Each role must have a unique name.
 */
@Entity
@Table(name = "roles")
public class Role {

    /**
     * Primary key of the role.
     * Automatically generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    /**
     * Name of the role.
     * Must be unique and not null.
     *
     * Example values:
     * - ADMIN
     * - STUDENT_ASSISTANT
     */
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    // ================= GETTERS =================

    public Integer getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }
}