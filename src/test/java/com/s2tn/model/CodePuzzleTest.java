package com.s2tn.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Executable unit tests for CodePuzzle.
 * Each test specifies concrete inputs and verifies concrete expected outputs.
 */
class CodePuzzleTest {

    // ====================== enterInput & state transitions ======================

    @Test
    @DisplayName("enterInput(null) -> invalid result; internal state remains INIT")
    void enterInput_nullInput_doesNotChangeStateFromInit() {
        // Setup
        CodePuzzle puzzle = new CodePuzzle();
        assertEquals(PuzzleState.INIT, puzzle.getState(), "Precondition: starts INIT");

        // Action (Input)
        ValidationResult result = puzzle.enterInput(null);

        // Expected Output
        assertNotNull(result, "ValidationResult should not be null");
        assertEquals(PuzzleState.INIT, puzzle.getState(), "Null input should not change internal state");
    }

    @Test
    @DisplayName("enterInput('   ') blank -> invalid result; internal state remains INIT")
    void enterInput_blankInput_doesNotChangeStateFromInit() {
        // Setup
        CodePuzzle puzzle = new CodePuzzle();

        // Action (Input)
        ValidationResult result = puzzle.enterInput("   ");

        // Expected Output
        assertNotNull(result);
        assertEquals(PuzzleState.INIT, puzzle.getState(), "Blank input should not change internal state");
    }

    @Test
    @DisplayName("enterInput('nope') wrong -> invalid; state becomes IN_PROGRESS")
    void enterInput_wrongCode_setsInProgress() {
        // Setup
        CodePuzzle puzzle = new CodePuzzle();

        // Action (Input)
        ValidationResult result = puzzle.enterInput("nope");

        // Expected Output
        assertNotNull(result);
        assertEquals(PuzzleState.IN_PROGRESS, puzzle.getState(), "Wrong input should set state to IN_PROGRESS");
    }

    @Test
    @DisplayName("enterInput('victory') default accepted -> valid; state becomes SOLVED")
    void enterInput_defaultAccepted_victory_solves() {
        // Setup
        CodePuzzle puzzle = new CodePuzzle();

        // Action (Input)
        ValidationResult result = puzzle.enterInput("victory");

        // Expected Output
        assertNotNull(result);
        assertEquals(PuzzleState.SOLVED, puzzle.getState(), "Default 'victory' should SOLVE the puzzle");
    }

    @Test
    @DisplayName("enterInput('  ViCtOrY  ') trimmed/case-insensitive -> valid; state SOLVED")
    void enterInput_trimmedCaseInsensitive_solves() {
        // Setup
        CodePuzzle puzzle = new CodePuzzle();

        // Action (Input)
        ValidationResult result = puzzle.enterInput("  ViCtOrY  ");

        // Expected Output
        assertNotNull(result);
        assertEquals(PuzzleState.SOLVED, puzzle.getState(), "Trimmed + case-insensitive match should SOLVE");
    }

    // ====================== accepted code management & normalization ======================

    @Test
    @DisplayName("addAcceptedCode('  HELLO  ') then input ' hello ' -> SOLVED (normalized)")
    void addAcceptedCode_normalization_allowsTrimCaseInsensitiveMatches() {
        // Setup
        CodePuzzle puzzle = new CodePuzzle();
        puzzle.addAcceptedCode("  HELLO  ");

        // Action
        puzzle.enterInput(" hello ");

        // Expected Output
        assertEquals(PuzzleState.SOLVED, puzzle.getState(), "Added code should be matched case-insensitively and trimmed");
    }

    @Test
    @DisplayName("Constructor with codes { '  KEY  ', 'OpEnSesame' } accepts ' key ' and ' openSESAme '")
    void constructor_normalizesCodes_andAcceptsVariants() {
        // Setup
        Set<String> codes = new HashSet<>();
        codes.add("  KEY  ");
        codes.add("OpEnSesame");

        // Action + Expected Output (first code)
        CodePuzzle puzzle1 = new CodePuzzle("Custom", codes);
        puzzle1.enterInput(" key ");
        assertEquals(PuzzleState.SOLVED, puzzle1.getState(), "Should match normalized 'KEY'");

        // Action + Expected Output (second code)
        CodePuzzle puzzle2 = new CodePuzzle("Custom", codes);
        puzzle2.enterInput(" openSESAme ");
        assertEquals(PuzzleState.SOLVED, puzzle2.getState(), "Should match normalized 'OpEnSesame'");
    }

    @Test
    @DisplayName("addAcceptedCode(null) has no effect; 'victory' remains accepted")
    void addAcceptedCode_null_hasNoEffect() {
        // Setup
        CodePuzzle puzzle = new CodePuzzle();
        puzzle.addAcceptedCode(null);

        // Action
        puzzle.enterInput("victory");

        // Expected Output
        assertEquals(PuzzleState.SOLVED, puzzle.getState(), "Null add should not break default acceptance of 'victory'");
    }

    // ====================== hint & prompt accessors ======================

    @Test
    @DisplayName("Default hint is present; setHint updates; null becomes empty string")
    void hint_default_andSetHint_andNullCoercedToEmpty() {
        // Setup
        CodePuzzle puzzle = new CodePuzzle();

        // Expected Output (default)
        assertEquals("A single triumphant word will open the console.", puzzle.getHint());

        // Action + Expected Output (set)
        puzzle.setHint("Try the winning word");
        assertEquals("Try the winning word", puzzle.getHint());

        // Action + Expected Output (null -> empty)
        puzzle.setHint(null);
        assertEquals("", puzzle.getHint(), "Null hint should be stored as empty string");
    }

    @Test
    @DisplayName("setCodePrompt/getCodePrompt round-trip stores and returns prompt text")
    void codePrompt_roundTrip_getsBackWhatWasSet() {
        // Setup
        CodePuzzle puzzle = new CodePuzzle();
        assertNull(puzzle.getCodePrompt(), "Precondition: default codePrompt is null");

        // Action
        puzzle.setCodePrompt("Enter the sacred code:");

        // Expected Output
        assertEquals("Enter the sacred code:", puzzle.getCodePrompt());
    }

    // ====================== achievement condition ======================
    // Condition: SOLVED && hintsUsed==0 && duration!=null && duration<=10s

    @Test
    @DisplayName("Achievement TRUE when SOLVED, hintsUsed=0, and duration<=10s")
    void achievement_true_whenSolvedNoHintsAndWithinTenSeconds() {
        // Setup
        CodePuzzle puzzle = new CodePuzzle();
        puzzle.enterInput("victory"); // SOLVED

        // Action + Expected Output
        assertTrue(
                puzzle.checkSpecificAchievementCondition(null, Duration.ofSeconds(10), 0, 0),
                "Exactly 10 seconds should qualify"
        );
        assertTrue(
                puzzle.checkSpecificAchievementCondition(null, Duration.ofSeconds(3), 0, 999),
                "Under 10 seconds should qualify"
        );
    }

    @Test
    @DisplayName("Achievement FALSE when any hintsUsed > 0")
    void achievement_false_whenHintsWereUsed() {
        // Setup
        CodePuzzle puzzle = new CodePuzzle();
        puzzle.enterInput("victory"); // SOLVED

        // Action
        boolean achieved = puzzle.checkSpecificAchievementCondition(null, Duration.ofSeconds(5), 1, 0);

        // Expected Output
        assertFalse(achieved, "Any hints used should fail the achievement");
    }

    @Test
    @DisplayName("Achievement FALSE when duration > 10s")
    void achievement_false_whenDurationExceedsTenSeconds() {
        // Setup
        CodePuzzle puzzle = new CodePuzzle();
        puzzle.enterInput("victory"); // SOLVED

        // Action
        boolean achieved = puzzle.checkSpecificAchievementCondition(null, Duration.ofSeconds(11), 0, 0);

        // Expected Output
        assertFalse(achieved, "More than 10 seconds should fail the achievement");
    }

    @Test
    @DisplayName("Achievement FALSE when duration is null")
    void achievement_false_whenDurationIsNull() {
        // Setup
        CodePuzzle puzzle = new CodePuzzle();
        puzzle.enterInput("victory"); // SOLVED

        // Action
        boolean achieved = puzzle.checkSpecificAchievementCondition(null, null, 0, 0);

        // Expected Output
        assertFalse(achieved, "Null duration should fail the achievement");
    }

    @Test
    @DisplayName("Achievement FALSE when not SOLVED even if hintsUsed=0 and duration<=10s")
    void achievement_false_whenNotSolvedDespiteGoodParams() {
        // Setup
        CodePuzzle puzzle = new CodePuzzle();
        puzzle.enterInput("nope"); // IN_PROGRESS

        // Action
        boolean achieved = puzzle.checkSpecificAchievementCondition(null, Duration.ofSeconds(2), 0, 500);

        // Expected Output
        assertFalse(achieved, "Must be SOLVED to qualify");
    }
}
