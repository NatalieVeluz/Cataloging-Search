package com.catalog.service;

/**
 * CutterNumberService
 *
 * This service is responsible for generating a Cutter number
 * based on the author's name.
 *
 * The Cutter number is used in library classification systems
 * to help organize books alphabetically by author within
 * the same subject classification.
 *
 * In the Cataloging Search Resources Platform,
 * this is used during:
 * - Manual book entry
 * - Book updates
 * - Automated cataloging processes
 */
public interface CutterNumberService {

    /**
     * Generates a Cutter number based on the provided author name.
     *
     * Example:
     * Author: "Smith"
     * Generated Cutter: S65 (depending on algorithm rules)
     *
     * @param author Author's name
     * @return Generated Cutter number
     */
    String generate(String author);
}