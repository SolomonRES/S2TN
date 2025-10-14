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

}
