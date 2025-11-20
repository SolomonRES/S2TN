package com.s2tn.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShapeMatchPuzzleTest {

    @Test
    void enterInput_emptyInput_setsNoInputHint() {
        ShapeMatchPuzzle puzzle = new ShapeMatchPuzzle();
        ValidationResult res = puzzle.enterInput("   ");  // blank user input

        assertFalse(res.isValid());
        assertEquals("no input", puzzle.getHint());
    }

    @Test
    void enterInput_exactMatch_returnsAlignedAndValid() {
        ShapeMatchPuzzle puzzle = new ShapeMatchPuzzle();
        puzzle.setTarget("A", 10, 20, 0);
        puzzle.setTarget("B", 30, 40, 90);

        ValidationResult res = puzzle.enterInput("A:10,20,0; B:30,40,90");

        assertTrue(res.isValid());
        assertEquals("aligned", puzzle.getHint());
    }

    @Test
    void enterInput_beyondPositionTolerance_reportsOffByPx() {
        ShapeMatchPuzzle puzzle = new ShapeMatchPuzzle();
        puzzle.setTarget("A", 10, 20, 0);
        puzzle.setTarget("B", 30, 40, 90);

        // A is 5.1 px away (default pos tol = 5.0) → invalid
        ValidationResult res = puzzle.enterInput("A:15.1,20,0; B:30,40,90");

        assertFalse(res.isValid());
        assertTrue(puzzle.getHint().startsWith("A off by "));
        assertTrue(puzzle.getHint().endsWith("px"));
    }

    @Test
    void enterInput_beyondAngleTolerance_reportsAngleOff() {
        ShapeMatchPuzzle puzzle = new ShapeMatchPuzzle();
        puzzle.setTarget("A", 10, 20, 0);
        puzzle.setTarget("B", 30, 40, 90);

        // A is 6° off (default angle tol = 5°) → invalid
        ValidationResult res = puzzle.enterInput("A:10,20,6; B:30,40,90");

        assertFalse(res.isValid());
        assertTrue(puzzle.getHint().contains("angle off"));
        assertTrue(puzzle.getHint().endsWith("°"));
    }

    @Test
    void enterInput_angleOnToleranceBoundary_stillValid() {
        ShapeMatchPuzzle puzzle = new ShapeMatchPuzzle();
        puzzle.setTarget("A", 10, 20, 0);
        puzzle.setTarget("B", 30, 40, 90);

        // exactly 5° off → allowed (code fails only when > 5)
        ValidationResult res = puzzle.enterInput("A:10,20,5; B:30,40,90");

        assertTrue(res.isValid());
        assertEquals("aligned", puzzle.getHint());
    }

    @Test
    void enterInput_missingLabel_reportsMissingShapeB() {
        ShapeMatchPuzzle puzzle = new ShapeMatchPuzzle();
        puzzle.setTarget("A", 10, 20, 0);
        puzzle.setTarget("B", 30, 40, 90);

        // omit B entirely
        ValidationResult res = puzzle.enterInput("A:10,20,0");

        assertFalse(res.isValid());
        assertTrue(puzzle.getHint().startsWith("missing shape: B"));
    }

    @Test
    void enterInput_badFormat_givesHelpfulHint() {
        ShapeMatchPuzzle puzzle = new ShapeMatchPuzzle();
        // set at least one valid target to avoid “no targets” path
        puzzle.setTarget("A", 10, 20, 0);

        ValidationResult res = puzzle.enterInput("nonsense");

        assertFalse(res.isValid());
        assertTrue(puzzle.getHint().startsWith("bad format"));
    }

    @Test
    void enterInput_noTargetsSet_reportsNoTargetsHint() {
        ShapeMatchPuzzle puzzle = new ShapeMatchPuzzle();

        ValidationResult res = puzzle.enterInput("A:10,20,0");

        assertFalse(res.isValid());
        // implementation sets lastHint = "no targets"
        assertEquals("no targets", puzzle.getHint());
    }

    @Test
    void setTolerance_negativeValues_ignored_keepDefaultsBehavior() {
        ShapeMatchPuzzle puzzle = new ShapeMatchPuzzle();
        puzzle.setTarget("A", 10, 20, 0);
        puzzle.setTarget("B", 30, 40, 90);

        // Try to set invalid (negative) tolerances — should be ignored
        puzzle.setTolerance(-5.0, -10.0);

        // If defaults (5px / 5°) are still in effect, both of these should be INVALID:

        // 5.1 px away → invalid (would become valid only if posTol had been increased)
        ValidationResult posRes = puzzle.enterInput("A:15.1,20,0; B:30,40,90");
        assertFalse(posRes.isValid());

        // 5.5° off → invalid (would become valid only if angTol had been increased)
        ValidationResult angRes = puzzle.enterInput("A:10,20,5.5; B:30,40,90");
        assertFalse(angRes.isValid());
    }
}
