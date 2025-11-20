package com.s2tn.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ValidationResultTest {

    @Test
    @DisplayName("valid() factory should create a valid result with correct properties")
    void valid() {
        ValidationResult result = ValidationResult.valid("Correct!", PuzzleState.SOLVED);
        assertTrue(result.isValid());
        assertEquals("Correct!", result.getMessage());
        assertEquals(PuzzleState.SOLVED, result.getNewState());
    }

    @Test
    @DisplayName("valid() factory should handle null inputs gracefully")
    void valid_withNulls() {
        ValidationResult result = ValidationResult.valid(null, null);
        assertTrue(result.isValid());
        assertEquals("", result.getMessage(), "Null message should default to empty string.");
        assertEquals(PuzzleState.INIT, result.getNewState(), "Null state should default to INIT.");
    }

    @Test
    @DisplayName("invalidFormat() factory should create an invalid result with correct properties")
    void invalidFormat() {
        ValidationResult result = ValidationResult.invalidFormat("Try again.", PuzzleState.IN_PROGRESS);
        assertFalse(result.isValid());
        assertEquals("Try again.", result.getMessage());
        assertEquals(PuzzleState.IN_PROGRESS, result.getNewState());
    }

    @Test
    @DisplayName("invalidFormat() factory should handle null inputs gracefully")
    void invalidFormat_withNulls() {
        ValidationResult result = ValidationResult.invalidFormat(null, null);
        assertFalse(result.isValid());
        assertEquals("", result.getMessage(), "Null message should default to empty string.");
        assertEquals(PuzzleState.INIT, result.getNewState(), "Null state should default to INIT.");
    }

    @Test
    @DisplayName("isValid() should return the correct boolean status")
    void isValid() {
        ValidationResult validResult = ValidationResult.valid("OK", PuzzleState.SOLVED);
        assertTrue(validResult.isValid());

        ValidationResult invalidResult = ValidationResult.invalidFormat("FAIL", PuzzleState.IN_PROGRESS);
        assertFalse(invalidResult.isValid());
    }

    @Test
    @DisplayName("toString() should return a descriptive string")
    void testToString() {
        ValidationResult validResult = ValidationResult.valid("Correct!", PuzzleState.SOLVED);
        String expectedValid = "ValidationResult{valid=true, message='Correct!', newState=SOLVED}";
        assertEquals(expectedValid, validResult.toString());

        ValidationResult invalidResult = ValidationResult.invalidFormat("Try again.", PuzzleState.IN_PROGRESS);
        String expectedInvalid = "ValidationResult{valid=false, message='Try again.', newState=IN_PROGRESS}";
        assertEquals(expectedInvalid, invalidResult.toString());
    }
}