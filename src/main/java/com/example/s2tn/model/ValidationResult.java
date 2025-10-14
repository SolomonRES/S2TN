package com.example.s2tn.model;

public class ValidationResult {
    private boolean valid;
    private String message;
    private PuzzleState newState;

    public ValidationResult(boolean valid, String message, PuzzleState newState) {
        this.valid = valid;
        this.message = message;
        this.newState = newState;
    }
    public boolean isValid() {
        return valid;
    }
    public String getMessage() {
        return message;
    }
    public PuzzleState getNewState() {
        return newState;
    }

    // Need to add static factory methods for results
    public static ValidationResult correct(String message, PuzzleState newState) {
        return new ValidationResult(true, message, newState);
    }

    public static ValidationResult incorrect(String message, PuzzleState newState) {
        return new ValidationResult(false, message, newState);
    }

    public static ValidationResult invalidFormat(String message, PuzzleState newState) {
        return new ValidationResult(false, message, newState);
    }

}
