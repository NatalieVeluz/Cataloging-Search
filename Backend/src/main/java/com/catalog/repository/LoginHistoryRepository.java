package com.catalog.repository;

// Import the LoginHistory entity class
import com.catalog.entity.LoginHistory;

// Import JpaRepository which provides built-in CRUD operations
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * LoginHistoryRepository
 *
 * This interface is responsible for handling database operations
 * related to the LoginHistory entity.
 *
 * By extending JpaRepository, it automatically provides:
 * - save()        -> insert/update login history
 * - findById()    -> get login history by ID
 * - findAll()     -> get all login records
 * - deleteById()  -> delete login history by ID
 * - and many more built-in methods
 */
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Integer> {

    // No need to write any code here for basic CRUD operations.
    // Spring Data JPA automatically implements them.

}