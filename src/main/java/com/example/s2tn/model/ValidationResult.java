package com.example.s2tn.model;

/**
 * Represents the result of validating a puzzle input,
 * including whether it was valid, a message, and the resulting puzzle state.
 */
public class ValidationResult {

    private final boolean valid;
    private final String message;
    private final PuzzleState newState;

    /** Creates a new validation result with provided status, message, and state. */
    private ValidationResult(boolean valid, String message, PuzzleState newState) {
        this.valid = valid;
        this.message = message == null ? "" : message;
        this.newState = newState == null ? PuzzleState.INIT : newState;
    }

    /** Returns a successful validation result. */
    public static ValidationResult valid(String message, PuzzleState newState) {
        return new ValidationResult(true, message, newState);
    }

    /** Returns a failed validation result (invalid input or incorrect format). */
    public static ValidationResult invalidFormat(String message, PuzzleState newState) {
        return new ValidationResult(false, message, newState);
    }

    /** Returns true if the validation was successful. */
    public boolean isValid() { return valid; }

    /** Returns the validation message for user feedback. */
    public String getMessage() { return message; }

    /** Returns the resulting puzzle state after validation. */
    public PuzzleState getNewState() { return newState; }

    /** Returns a string representation of the validation result. */
    @Override
    public String toString() {
        return "ValidationResult{valid=" + valid +
               ", message='" + message + '\'' +
               ", newState=" + newState + '}';
    }
}
