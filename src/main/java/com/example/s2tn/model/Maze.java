package com.example.s2tn.model;

/**
 * Maze puzzle where the player moves from start to end position.
 * Lightweight, UML-aligned implementation.
 *
 * - Positions kept as strings (start/end/player).
 * - enterInput(...) updates playerPosition and returns a ValidationResult.
 * - hint is maintained and returned by getHint() (no unused-field warning).
 * - Compatible with ValidationResult factories OR (boolean,String,PuzzleState) ctor.
 */
public class Maze extends Puzzle {

    private String startPosition;
    private String endPosition;
    private String playerPosition;
    private String hint; // actively maintained

    public Maze() {
        this.startPosition  = "";
        this.endPosition    = "";
        this.playerPosition = "";
        this.hint           = "Set an end first";
    }

    // ----- accessors / setup -----
    public void setStartPosition(String pos)   {
        this.startPosition = pos == null ? "" : pos.trim();
        refreshHint();
    }
    public void setEndPosition(String pos)     {
        this.endPosition = pos == null ? "" : pos.trim();
        refreshHint();
    }
    public void setPlayerPosition(String pos)  {
        this.playerPosition = pos == null ? "" : pos.trim();
        refreshHint();
    }

    public String getStartPosition()  { return this.startPosition; }
    public String getEndPosition()    { return this.endPosition; }
    public String getPlayerPosition() { return this.playerPosition; }

    // ----- lightweight utility hooks -----
    public void displayPuzzle() {
        System.out.println("Maze: Player at " + playerPosition);
    }

    /** Append first letter of direction for a simple, testable move. */
    public String move(String direction) {
        if (direction == null || direction.isBlank()) return "invalid";
        playerPosition += direction.trim().toUpperCase().charAt(0);
        refreshHint();
        return playerPosition;
    }

    /** True iff userInput equals endPosition (case-insensitive). */
    public boolean checkAnswer(String userInput) {
        if (userInput == null) return false;
        return userInput.trim().equalsIgnoreCase(endPosition);
    }

    // ----- required Puzzle API -----
    public String getHint() {
        return hint;
    }

    @Override
    public ValidationResult enterInput(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            hint = "Empty input";
            return vr(false, "Empty input", PuzzleState.IN_PROGRESS);
        }

        this.playerPosition = userInput.trim();

        if (!endPosition.isEmpty() && playerPosition.equalsIgnoreCase(endPosition)) {
            hint = "You're at the end!";
            return vr(true, "Solved", PuzzleState.SOLVED);
        }

        refreshHint();
        return vr(true, "Position updated", PuzzleState.IN_PROGRESS);
    }

    @Override
    public boolean checkSpecificAchievementCondition(
            Achievement achievement,
            java.time.Duration elapsed,
            int movesMade,
            int hintsUsed) {
        // No Maze-specific achievements yet.
        return false;
    }

    // ----- helpers -----
    private void refreshHint() {
        if (endPosition.isEmpty()) {
            hint = "Set an end first";
        } else if (playerPosition.equalsIgnoreCase(endPosition)) {
            hint = "You're at the end!";
        } else {
            hint = "Keep moving toward " + endPosition;
        }
    }

    // Compatibility for different ValidationResult APIs.
    private ValidationResult vr(boolean ok, String msg, PuzzleState state) {
        try {
            java.lang.reflect.Method m = ValidationResult.class.getDeclaredMethod(
                    ok ? "valid" : "invalid", String.class, PuzzleState.class);
            return (ValidationResult) m.invoke(null, msg, state);
        } catch (Throwable ignore) {
            try {
                java.lang.reflect.Constructor<ValidationResult> ctor =
                        ValidationResult.class.getDeclaredConstructor(boolean.class, String.class, PuzzleState.class);
                if (!ctor.canAccess(null)) ctor.setAccessible(true);
                return ctor.newInstance(ok, msg, state);
            } catch (Throwable e) {
                throw new RuntimeException("ValidationResult API mismatch", e);
            }
        }
    }
}
