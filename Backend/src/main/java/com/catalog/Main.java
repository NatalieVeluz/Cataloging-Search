package com.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main
 *
 * Entry point of the Cataloging Search Resources Platform backend.
 *
 * This class bootstraps the Spring Boot application.
 *
 * Responsibilities:
 * - Initializes the Spring Application Context
 * - Enables component scanning
 * - Activates auto-configuration
 * - Starts embedded web server (Tomcat by default)
 *
 * Annotation Explanation:
 * @SpringBootApplication is a convenience annotation that combines:
 * - @Configuration → Marks this class as a source of bean definitions
 * - @EnableAutoConfiguration → Enables automatic configuration
 * - @ComponentScan → Scans for components in the package and subpackages
 *
 * Application Architecture:
 * - Backend: Spring Boot
 * - Database: MySQL
 * - Frontend: Angular (separate application)
 */
@SpringBootApplication
public class Main {

    /**
     * Main method that launches the Spring Boot application.
     *
     * @param args Command-line arguments passed during application startup
     */
    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
    }
}