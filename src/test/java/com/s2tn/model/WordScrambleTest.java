package com.s2tn.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the WordScramble class.
 * Each test provides input to methods and verifies the output matches expectations.
 */
class WordScrambleTest {

    // ---------------- checkAnswer Tests ----------------

    @Test
    @DisplayName("checkAnswer returns true when the input matches the solution (case-insensitive)")
    void checkAnswer_returnsTrueForCorrectAnswer() {
        // Setup
        WordScramble ws = new WordScramble();
        ws.setSolution("escape");

        // Test with different casing
        boolean result = ws.checkAnswer("EsCaPe");

        // Expected Output
        assertTrue(result, "Expected true when answer matches solution ignoring case");
    }

    @Test
    @DisplayName("checkAnswer returns false when the input is incorrect")
    void checkAnswer_returnsFalseForIncorrectAnswer() {
        // Setup
        WordScramble ws = new WordScramble();
        ws.setSolution("escape");

        // Input is different than the solution
        boolean result = ws.checkAnswer("escapes");

        // Expected Output
        assertFalse(result, "Expected false for an incorrect answer");
    }

    @Test
    @DisplayName("checkAnswer returns false when answer is null")
    void checkAnswer_returnsFalseWhenAnswerIsNull() {
        // Setup
        WordScramble ws = new WordScramble();
        ws.setSolution("escape");

        // null input
        boolean result = ws.checkAnswer(null);

        // Expected Output
        assertFalse(result, "Expected false when answer is null");
    }

    @Test
    @DisplayName("checkAnswer returns false when solution is not set (null)")
    void checkAnswer_returnsFalseWhenSolutionIsNull() {
        // Setup
        WordScramble ws = new WordScramble(); // solution is still null

        // Valid answer but no stored solution
        boolean result = ws.checkAnswer("escape");

        // Expected Output
        assertFalse(result, "Expected false when solution has not been set");
    }

    // ---------------- Getter/Setter Tests ----------------

    @Test
    @DisplayName("setScrambledWord and getScrambledWord correctly store and return the scrambled word")
    void scrambledWord_setterAndGetter_workCorrectly() {
        // Setup
        WordScramble ws = new WordScramble();
        ws.setScrambledWord("ESACEP");

        // Expected Output
        assertEquals("ESACEP", ws.getScrambledWord(), "Scrambled word should match the value set");
    }

    @Test
    @DisplayName("setSolution and getSolution correctly store and return the solution")
    void solution_setterAndGetter_workCorrectly() {
        // Setup
        WordScramble ws = new WordScramble();
        ws.setSolution("escape");

        // Expected Output
        assertEquals("escape", ws.getSolution(), "Solution should match the value set");
    }

    @Test
    @DisplayName("getHint returns null by default when no hint is set")
    void getHint_returnsNullByDefault() {
        // Setup
        WordScramble ws = new WordScramble();

        // Expected Output
        assertNull(ws.getHint(), "Hint should be null by default");
    }

    // ---------------- setTitle Exception Test ----------------

    @Test
    @DisplayName("setTitle throws UnsupportedOperationException with correct message")
    void setTitle_throwsUnsupportedOperationException() {
        // Setup
        WordScramble ws = new WordScramble();

        // Expected Output
        Exception exception = assertThrows(UnsupportedOperationException.class,
                () -> ws.setTitle("Puzzle Title"),
                "Expected setTitle to throw UnsupportedOperationException");

        assertEquals("Unimplemented method 'setTitle'", exception.getMessage(),
                "Exception message should match method implementation");
    }
}
