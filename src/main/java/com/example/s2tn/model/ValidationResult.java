package com.example.s2tn.model;

public class ValidationResult {

    private final boolean valid;
    private final String message;
    private final PuzzleState newState;

    private ValidationResult(boolean valid, String message, PuzzleState newState) {
        this.valid = valid;
        this.message = message == null ? "" : message;
        this.newState = newState == null ? PuzzleState.INIT : newState;
    }

    public static ValidationResult valid(String message, PuzzleState newState) {
        return new ValidationResult(true, message, newState);
    }

    public static ValidationResult invalidFormat(String message, PuzzleState newState) {
        return new ValidationResult(false, message, newState);
    }

    public boolean isValid() { return valid; }
    public String getMessage() { return message; }
    public PuzzleState getNewState() { return newState; }

    @Override
    public String toString() {
        return "ValidationResult{valid=" + valid +
               ", message='" + message + '\'' +
               ", newState=" + newState + '}';
    }
}
