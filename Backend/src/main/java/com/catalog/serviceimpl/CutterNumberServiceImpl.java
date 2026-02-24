package com.catalog.serviceimpl;

import com.catalog.service.CutterNumberService;
import org.springframework.stereotype.Service;

/**
 * CutterNumberServiceImpl
 *
 * Implementation of the CutterNumberService interface.
 *
 * This service generates a simplified Cutter number
 * based on an input string (typically the author's name).
 *
 * Current Logic:
 * - Extracts the first character of the input
 * - Converts it to uppercase
 * - Appends a fixed numeric sequence ("123")
 *
 * This implementation serves as a basic placeholder algorithm
 * for demonstration and system functionality purposes.
 *
 * Note:
 * In a production-grade library system, the Cutter number
 * should follow the official Cutter-Sanborn table to ensure
 * standardized and accurate classification.
 */
@Service
public class CutterNumberServiceImpl implements CutterNumberService {

    /**
     * Generates a Cutter number from the provided input.
     *
     * Behavior:
     * - If the input is null or blank, a default value "X000" is returned.
     * - Otherwise, the first character of the input is extracted,
     *   converted to uppercase, and appended with "123".
     *
     * Example:
     * Input:  "Smith"
     * Output: "S123"
     *
     * @param input Author name or title used as basis for Cutter number
     * @return Generated Cutter number string
     */
    @Override
    public String generate(String input) {

        if (input == null || input.isBlank()) {
            return "X000";
        }

        return input.substring(0, 1).toUpperCase() + "123";
    }
}