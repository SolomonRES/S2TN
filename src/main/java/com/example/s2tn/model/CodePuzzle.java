package com.example.s2tn.model;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;


public class CodePuzzle extends Puzzle {
    private final Set<String> acceptedCodes = new HashSet<>();
    private String codePrompt; 

    public void setCodePrompt(String text) {
        this.codePrompt = text;
    }

    public String getCodePrompt() {
        return codePrompt;
    }

    public CodePuzzle() {
        setTitle("Code Puzzle");
        setState(PuzzleState.INIT);
        setMaxHints(0);
        // acceptedCodes.add("victory"); 
    }

    public CodePuzzle(String title, Set<String> codes) {
        setTitle(title == null ? "Code Puzzle" : title);
        setState(PuzzleState.INIT);
        setMaxHints(0);
        if (codes != null) {
            for (String c : codes) {
                if (c != null) acceptedCodes.add(c.trim().toLowerCase());
            }
        }
    }

    public void addAcceptedCode(String code) {
        if (code != null) acceptedCodes.add(code.trim().toLowerCase());
    }

    // this is for the facade/driver
    @Override
    public ValidationResult enterInput(String input) {
        if (input == null || input.isBlank()) {
            return ValidationResult.invalidFormat("Enter a code.", PuzzleState.IN_PROGRESS);
        }
        boolean ok = acceptedCodes.contains(input.trim().toLowerCase());
        if (ok) {
            setState(PuzzleState.SOLVED);
            return ValidationResult.valid("Correct code.", PuzzleState.SOLVED);
        } else {
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.invalidFormat("Incorrect code.", PuzzleState.IN_PROGRESS);
        }
    }

    // this is for abstract puzzle 
    @Override
    protected boolean checkSpecificAchievementCondition(
            Achievement achievement, Duration duration, int hintsUsed, int currentScore) {
        return getState() == PuzzleState.SOLVED
                && hintsUsed == 0
                && duration != null
                && duration.getSeconds() <= 10;
    }
}
