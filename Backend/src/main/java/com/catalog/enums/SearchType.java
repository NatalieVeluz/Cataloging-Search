package com.catalog.enums;

/**
 * SearchType Enumeration
 *
 * This enum defines the supported search categories
 * within the system.
 *
 * It is used to classify and store the type of search
 * performed by a user in the SearchLog entity.
 *
 * The enum values are stored as strings in the database
 * when used with @Enumerated(EnumType.STRING).
 */
public enum SearchType {

    /**
     * Search performed using an ISBN value.
     * Used for exact book identification.
     */
    ISBN,

    /**
     * Search performed using a book title.
     * Supports keyword-based searching.
     */
    TITLE,

    /**
     * Search performed using an author name.
     * Allows searching by contributor.
     */
    AUTHOR
}