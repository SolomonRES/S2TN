package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Sliding-shapes puzzle (UML subclass of Puzzle).
 * Minimal mechanics; integrates PuzzleState transitions and Difficulty move cap.
 */
public class SlideShapePuzzle extends Puzzle {

    private List<String> startConfiguration;
    private List<String> endConfiguration;
    private List<String> currentConfiguration;
    private int movesMade;

    // ---------- ctors ----------
    public SlideShapePuzzle() {
        this.startConfiguration = new ArrayList<>();
        this.endConfiguration = new ArrayList<>();
        this.currentConfiguration = new ArrayList<>();
        this.movesMade = 0;
    }

    public SlideShapePuzzle(List<String> startConfiguration, List<String> endConfiguration) {
        this.startConfiguration = (startConfiguration == null) ? new ArrayList<>() : new ArrayList<>(startConfiguration);
        this.endConfiguration   = (endConfiguration   == null) ? new ArrayList<>() : new ArrayList<>(endConfiguration);
        this.currentConfiguration = new ArrayList<>(this.startConfiguration);
        this.movesMade = 0;
    }

    // ---------- getters / setters ----------
    public List<String> getStartConfiguration() { return new ArrayList<>(startConfiguration); }
    public List<String> getEndConfiguration()   { return new ArrayList<>(endConfiguration); }
    public List<String> getCurrentConfiguration(){ return new ArrayList<>(currentConfiguration); }
    public int getMovesMade() { return movesMade; }

    public void setStartConfiguration(List<String> startConfiguration) {
        this.startConfiguration = (startConfiguration == null) ? new ArrayList<>() : new ArrayList<>(startConfiguration);
        if (getState() == PuzzleState.NOT_STARTED) {
            this.currentConfiguration = new ArrayList<>(this.startConfiguration);
        }
    }

    public void setEndConfiguration(List<String> endConfiguration) {
        this.endConfiguration = (endConfiguration == null) ? new ArrayList<>() : new ArrayList<>(endConfiguration);
    }

    // ---------- simple helpers ----------
    /** True when the current positions match the target configuration. */
    public boolean isSolved() {
        return currentConfiguration.equals(endConfiguration);
    }

    /**
     * Minimal slide: validates input + enforces Difficulty move cap.
     * Example: slide("A","UP")
     */
    public boolean slide(String shape, String direction) {
        if (shape == null || shape.isBlank() || direction == null || direction.isBlank()) return false;

        String dir = direction.trim().toUpperCase();
        if (!(dir.equals("UP") || dir.equals("DOWN") || dir.equals("LEFT") || dir.equals("RIGHT"))) return false;

        // Difficulty cap (use +1 to check the next move)
        if (!canMakeMove(movesMade + 1)) {
            return false;
        }

        movesMade++;
        // TODO: update currentConfiguration when real rules are added
        return true;
    }

    /** Simple hint text. */
    public String getHint() {
        return "Try moving a border piece toward an empty space.";
    }

    /**
     * Commands:
     *  - "SLIDE id:DIR"   e.g., "SLIDE A:UP"
     *  - "CHECK"
     */
    @Override
    public ValidationResult enterInput(String input) {
        if (getState() == PuzzleState.NOT_STARTED) {
            start(); // NOT_STARTED -> IN_PROGRESS
        }

        if (input == null || input.isBlank()) {
            markFailed();
            return new ValidationResult(false, "Empty input.", PuzzleState.FAILED);
        }

        String trimmed = input.trim();

        if (trimmed.toUpperCase().startsWith("SLIDE ")) {
            String rest = trimmed.substring(6).trim();
            String[] parts = rest.split(":", 2);
            if (parts.length != 2) {
                markFailed();
                return new ValidationResult(false, "Use 'SLIDE <id>:<UP|DOWN|LEFT|RIGHT>'", PuzzleState.FAILED);
            }
            boolean ok = slide(parts[0].trim(), parts[1].trim());
            if (!ok) {
                markFailed();
                return new ValidationResult(false, "Invalid slide or max moves reached.", PuzzleState.FAILED);
            }
            if (isSolved()) {
                markSolved();
                return new ValidationResult(true, "Puzzle solved!", PuzzleState.SOLVED);
            }
            return new ValidationResult(false, "Move accepted.", PuzzleState.IN_PROGRESS);
        }

        if (trimmed.equalsIgnoreCase("CHECK")) {
            if (isSolved()) {
                markSolved();
                return new ValidationResult(true, "Puzzle solved!", PuzzleState.SOLVED);
            } else {
                markFailed();
                return new ValidationResult(false, "Not solved yet.", PuzzleState.FAILED);
            }
        }

        markFailed();
        return new ValidationResult(false, "Unknown command. Use 'SLIDE id:DIR' or 'CHECK'.", PuzzleState.FAILED);
    }
}
