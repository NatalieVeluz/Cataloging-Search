package com.catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate Configuration Class
 *
 * This configuration defines a RestTemplate bean that is used
 * to perform HTTP requests to external APIs such as:
 * - Library of Congress
 * - Google Books API
 * - Open Library API
 *
 * The RestTemplate instance allows the application to retrieve
 * bibliographic metadata from external services.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates and registers a RestTemplate bean
     * in the Spring application context.
     *
     * This bean can be injected into services that need
     * to call external REST APIs.
     *
     * @return RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}