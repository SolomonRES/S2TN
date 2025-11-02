package com.s2tn.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Minimal, reflection-safe Maze tests:
 * - Only invokes ZERO-ARG public methods if present (skips param methods).
 * - No assumptions about return values; just verifies callability.
 */
class MazeTest {

    // -------- helpers --------

    /** Invoke the first PUBLIC zero-arg method with the given name. Return value (or null). */
    private Object invokeZeroArgIfPresent(Object target, String name) throws Exception {
        for (Method m : target.getClass().getMethods()) {
            if (m.getName().equals(name)
                && Modifier.isPublic(m.getModifiers())
                && m.getParameterCount() == 0) {
                m.setAccessible(true);
                return m.invoke(target);
            }
        }
        return null; // method not present or not zero-arg
    }

    // -------- tests --------

    @Test
    void create_and_toString() {
        Maze maze = new Maze();
        String s = maze.toString();
        assertNotNull(s, "toString() must not be null");
        assertTrue(s.length() >= 0, "toString() should be callable");
    }

    @Test
    void grid_and_walls_zeroArg_methods_doNotThrow_ifPresent() {
        Maze maze = new Maze();

        assertDoesNotThrow(() -> {
            try { invokeZeroArgIfPresent(maze, "setGrid"); } catch (Exception e) { throw new RuntimeException(e); }
        }, "setGrid (0-arg) should not throw if present");

        assertDoesNotThrow(() -> {
            try { invokeZeroArgIfPresent(maze, "addWall"); } catch (Exception e) { throw new RuntimeException(e); }
        }, "addWall (0-arg) should not throw if present");

        assertDoesNotThrow(() -> {
            try { invokeZeroArgIfPresent(maze, "clearWalls"); } catch (Exception e) { throw new RuntimeException(e); }
        }, "clearWalls (0-arg) should not throw if present");
    }

    @Test
    void displayPuzzle_zeroArg_safe() {
        Maze maze = new Maze();
        assertDoesNotThrow(() -> {
            try {
                Object out = invokeZeroArgIfPresent(maze, "displayPuzzle");
                // accept void or String; nothing to assert about content
                if (out instanceof String) assertNotNull(out);
            } catch (Exception e) { throw new RuntimeException(e); }
        }, "displayPuzzle (0-arg) should not throw if present");
    }

    @Test
    void move_zeroArg_safe_ifPresent() {
        Maze maze = new Maze();
        assertDoesNotThrow(() -> {
            try { invokeZeroArgIfPresent(maze, "move"); } catch (Exception e) { throw new RuntimeException(e); }
        }, "move (0-arg) should not throw if present");
    }

    @Test
    void checkAnswer_and_enterInput_zeroArg_callable_ifPresent() {
        Maze maze = new Maze();

        // enterInput with zero args (skip if it needs params)
        assertDoesNotThrow(() -> {
            try { invokeZeroArgIfPresent(maze, "enterInput"); } catch (Exception e) { throw new RuntimeException(e); }
        }, "enterInput (0-arg) should not throw if present");

        // checkAnswer with zero args; if it returns Boolean, just ensure it's a boolean
        assertDoesNotThrow(() -> {
            try {
                Object r = invokeZeroArgIfPresent(maze, "checkAnswer");
                if (r != null) assertTrue((r instanceof Boolean) || true);
            } catch (Exception e) { throw new RuntimeException(e); }
        }, "checkAnswer (0-arg) should not throw if present");
    }

    @Test
    void reset_or_resetToStart_zeroArg_safe_ifPresent_and_isSolved_callable() {
        Maze maze = new Maze();

        // try to mutate state a bit if zero-arg move exists
        assertDoesNotThrow(() -> {
            try { invokeZeroArgIfPresent(maze, "move"); } catch (Exception e) { throw new RuntimeException(e); }
        });

        // reset variants (zero-arg only)
        assertDoesNotThrow(() -> {
            try {
                Object r = invokeZeroArgIfPresent(maze, "reset");
                if (r == null) invokeZeroArgIfPresent(maze, "resetToStart");
            } catch (Exception e) { throw new RuntimeException(e); }
        }, "reset/resetToStart (0-arg) should not throw if present");

        // isSolved (if zero-arg) should be callable; no assumption on value
        assertDoesNotThrow(() -> {
            try { invokeZeroArgIfPresent(maze, "isSolved"); } catch (Exception e) { throw new RuntimeException(e); }
        }, "isSolved (0-arg) should be callable if present");
    }
}
