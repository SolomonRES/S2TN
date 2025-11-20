package com.s2tn.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Executable unit tests for Riddle.
 * Each test specifies concrete inputs and verifies concrete expected outputs.
 */
class RiddleTest {

    // ====================== enterInput ======================

    @Test
    @DisplayName("enterInput(null) -> invalid; state becomes IN_PROGRESS")
    void enterInput_nullInput_setsInProgress() {
        // Setup
        Riddle riddle = new Riddle("Riddle", "What am I?", "shadow", "A thing with light");
        assertEquals(PuzzleState.INIT, riddle.getState(), "Precondition: state starts as INIT");

        // Action (Input)
        ValidationResult result = riddle.enterInput(null);

        // Expected Output
        assertNotNull(result, "ValidationResult should not be null");
        // (We verify the observable system output via state transition)
        assertEquals(PuzzleState.IN_PROGRESS, riddle.getState(), "Null input should move state to IN_PROGRESS");
    }

    @Test
    @DisplayName("enterInput('shadow') exact -> valid; state becomes SOLVED")
    void enterInput_exactCorrectAnswer_solvesPuzzle() {
        // Setup
        Riddle riddle = new Riddle("Riddle", "What am I?", "shadow");

        // Action (Input)
        ValidationResult result = riddle.enterInput("shadow");

        // Expected Output
        assertNotNull(result);
        assertEquals(PuzzleState.SOLVED, riddle.getState(), "Exact correct answer should SOLVE the puzzle");
    }

    @Test
    @DisplayName("enterInput('  ShAdOw  ') trimmed/case-insensitive -> valid; state SOLVED")
    void enterInput_trimmedAndCaseInsensitive_correct_solvesPuzzle() {
        // Setup
        Riddle riddle = new Riddle("Riddle", "What am I?", "shadow");

        // Action (Input)
        ValidationResult result = riddle.enterInput("  ShAdOw  ");

        // Expected Output
        assertNotNull(result);
        assertEquals(PuzzleState.SOLVED, riddle.getState(), "Correct answer ignoring case/whitespace should SOLVE");
    }

    @Test
    @DisplayName("enterInput('umbrella') wrong -> invalid; state becomes IN_PROGRESS")
    void enterInput_incorrectAnswer_setsInProgress() {
        // Setup
        Riddle riddle = new Riddle("Riddle", "What am I?", "shadow");

        // Action (Input)
        ValidationResult result = riddle.enterInput("umbrella");

        // Expected Output
        assertNotNull(result);
        assertEquals(PuzzleState.IN_PROGRESS, riddle.getState(), "Wrong answer should set state to IN_PROGRESS");
    }

    // ====================== checkSpecificAchievementCondition ======================

    @Test
    @DisplayName("Achievement TRUE when state=SOLVED and hintsUsed=0 (duration/score ignored)")
    void achievement_true_whenSolvedWithZeroHints() {
        // Setup
        Riddle riddle = new Riddle("Riddle", "What am I?", "shadow");
        riddle.enterInput("shadow"); // move to SOLVED

        // Action (Input parameters)
        boolean achieved = riddle.checkSpecificAchievementCondition(
                /*achievement*/ null,
                /*duration*/ Duration.ofSeconds(1),
                /*hintsUsed*/ 0,
                /*currentScore*/ 50
        );

        // Expected Output
        assertTrue(achieved, "Should achieve when SOLVED with 0 hints used");
    }

    @Test
    @DisplayName("Achievement FALSE when state=SOLVED but hintsUsed>0")
    void achievement_false_whenHintsUsedPositive() {
        // Setup
        Riddle riddle = new Riddle("Riddle", "What am I?", "shadow");
        riddle.enterInput("shadow"); // SOLVED

        // Action
        boolean achieved = riddle.checkSpecificAchievementCondition(
                null, Duration.ofSeconds(2), /*hintsUsed*/ 1, /*score*/ 0
        );

        // Expected Output
        assertFalse(achieved, "Any hint usage should fail the achievement condition");
    }

    @Test
    @DisplayName("Achievement FALSE when not SOLVED even if hintsUsed=0")
    void achievement_false_whenNotSolved() {
        // Setup
        Riddle riddle = new Riddle("Riddle", "What am I?", "shadow");
        riddle.enterInput("umbrella"); // IN_PROGRESS

        // Action
        boolean achieved = riddle.checkSpecificAchievementCondition(
                null, Duration.ofSeconds(2), 0, 0
        );

        // Expected Output
        assertFalse(achieved, "Must be SOLVED to satisfy the achievement condition");
    }

    // ====================== getters/hint ======================

    @Test
    @DisplayName("getQuestion() and getHint() return the configured values")
    void getters_returnConfiguredValues() {
        // Setup
        Riddle riddle = new Riddle("Riddle", "What am I?", "shadow", "You see it with light");

        // Action + Expected Output
        assertEquals("What am I?", riddle.getQuestion());
        assertEquals("You see it with light", riddle.getHint());
    }
}
