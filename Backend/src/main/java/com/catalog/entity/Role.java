package com.catalog.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    // ===== GETTERS =====

    public Integer getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }
}
