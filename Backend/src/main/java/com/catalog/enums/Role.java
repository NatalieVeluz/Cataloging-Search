package com.catalog.enums;

/**
 * Role Enumeration
 *
 * This enum defines the available user roles
 * within the system.
 *
 * It is used to implement Role-Based Access Control (RBAC),
 * ensuring that users are granted permissions based on
 * their assigned role.
 *
 * The enum values are stored as strings in the database
 * when used with @Enumerated(EnumType.STRING).
 */
public enum Role {

    /**
     * Administrator role.
     *
     * Permissions include:
     * - Manual book creation
     * - Updating book metadata
     * - Deleting search logs
     * - Managing system-level operations
     */
    ADMIN,

    /**
     * Student Assistant role.
     *
     * Permissions include:
     * - Searching books
     * - Viewing search logs
     * - Pinning and unpinning books
     *
     * Restrictions:
     * - Cannot update book metadata
     * - Cannot delete search logs
     */
    STUDENT_ASSISTANT
}