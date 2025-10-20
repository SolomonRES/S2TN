package com.example.s2tn.model;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;


public class CodePuzzle extends Puzzle {
    private final Set<String> acceptedCodes = new HashSet<>();

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
            return ValidationResult.correct("Correct code.", PuzzleState.SOLVED);
        } else {
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.incorrect("Incorrect code.", PuzzleState.IN_PROGRESS);
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
