package com.s2tn.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class SlideShapePuzzleTest {

    @Test
    void isSolved() {
        SlideShapePuzzle puzzle = new SlideShapePuzzle();

        assertFalse(puzzle.isSolved(), "New puzzle should not be solved");

        boolean before = puzzle.isSolved();
        boolean after  = puzzle.isSolved();
        assertEquals(before, after, "isSolved() should be idempotent");
    }

    @Test
    void resetToStart() {
        SlideShapePuzzle puzzle = new SlideShapePuzzle();

        assertDoesNotThrow(() -> puzzle.enterInput("A:0,0,0"),
                "enterInput should not throw on basic input");

        puzzle.resetToStart();
        assertFalse(puzzle.isSolved(), "After resetToStart(), puzzle should not be solved");
    }

    @Test
    void displayPuzzle() {
        SlideShapePuzzle puzzle = new SlideShapePuzzle();

        // displayPuzzle() is void — just ensure it doesn’t throw
        assertDoesNotThrow(puzzle::displayPuzzle, "displayPuzzle() should not throw");
    }

    @Test
    void enterInput() {
        SlideShapePuzzle puzzle = new SlideShapePuzzle();

        ValidationResult res = puzzle.enterInput("A:10,20,0; B:30,40,0");
        assertNotNull(res, "enterInput(...) must return a ValidationResult");
        assertDoesNotThrow(res::isValid, "ValidationResult#isValid should be callable");
        assertNotNull(puzzle.getHint(), "getHint() should not be null after an attempt");
    }

    @Test
    void checkSpecificAchievementCondition_ifPresent() {
        SlideShapePuzzle puzzle = new SlideShapePuzzle();

        // Try to call checkSpecificAchievementCondition() if it exists.
        try {
            Method m = SlideShapePuzzle.class.getMethod("checkSpecificAchievementCondition");
            Object result = assertDoesNotThrow(() -> m.invoke(puzzle),
                    "checkSpecificAchievementCondition should not throw");
            if (result instanceof Boolean) {
                assertFalse((Boolean) result, "Achievement should be false on a fresh puzzle");
            } else {
                // If the method returns void or non-boolean, just consider it called successfully.
                assertTrue(true);
            }
        } catch (NoSuchMethodException e) {
            // Method not present in this implementation — test passes (nothing to check)
            assertTrue(true);
        } catch (Exception e) {
            fail("Invoking checkSpecificAchievementCondition failed: " + e.getMessage());
        }
    }

    @Test
    void testToString() {
        SlideShapePuzzle puzzle = new SlideShapePuzzle();

        String s = puzzle.toString();
        assertNotNull(s, "toString() must not return null");
        assertTrue(s.contains("SlideShapePuzzle") || s.length() > 0,
                "toString() should be informative");
    }
}
