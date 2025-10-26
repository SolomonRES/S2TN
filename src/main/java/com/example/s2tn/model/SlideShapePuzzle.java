package com.example.s2tn.model;

/**
 * Sliding puzzle with one empty slot "_".
 * Board is a flat list (row-major). You slide a tile into the empty slot.
 * Solved when the current board equals the goal board.
 *
 * enterInput examples:
 *   "A up"        (also accepts "slide A up")
 *   "B left"
 *   "solved"
 */
public class SlideShapePuzzle extends Puzzle {

    // fully qualify to avoid clash with com.example.s2tn.model.Map
    private final java.util.List<String> startConfiguration;
    private final java.util.List<String> endConfiguration;
    private final java.util.List<String> board;

    private final int rows;
    private final int cols;

    private int movesMade = 0;
    private String hint = "";

    // difficulty (kept flexible — no enum constants assumed)
    private Difficulty difficulty;                 // null -> default behavior (no cap)
    private int maxMoves = Integer.MAX_VALUE;      // cap only on HARD-like

    /** Creates a puzzle with explicit start/end configs and board dimensions. */
    public SlideShapePuzzle(java.util.List<String> start,
                            java.util.List<String> end,
                            int rows,
                            int cols) {
        if (rows <= 0 || cols <= 0) throw new IllegalArgumentException("rows/cols must be > 0");
        if (start == null || end == null) throw new IllegalArgumentException("configs cannot be null");
        if (start.size() != rows * cols || end.size() != rows * cols)
            throw new IllegalArgumentException("configs must be rows*cols long");

        this.rows = rows;
        this.cols = cols;

        this.startConfiguration = new java.util.ArrayList<>(start);
        this.endConfiguration   = new java.util.ArrayList<>(end);
        this.board              = new java.util.ArrayList<>(start);

        setState(PuzzleState.IN_PROGRESS);
        applyDifficulty(); // set defaults for whatever the current difficulty is (may be null)
    }

    /** Creates a simple default 3x3 slide puzzle configuration. */
    public SlideShapePuzzle() {
        this(
            java.util.Arrays.asList("A","B","C","D","E","F","G","_","H"),
            java.util.Arrays.asList("A","B","C","D","E","F","G","H","_"),
            3, 3
        );
    }

    // -------- basics --------

    /** Returns a copy of the current board tiles in row-major order. */
    public java.util.List<String> getBoard() { return new java.util.ArrayList<>(board); }

    /** Returns the number of moves made so far. */
    public int getMovesMade() { return movesMade; }

    /** Returns the latest hint/status message. */
    public String getHint() { return hint; }

    /** Returns true if the current board matches the end configuration. */
    public boolean isSolved() { return board.equals(endConfiguration); }

    /** Resets the board back to the original start configuration. */
    public void resetToStart() {
        board.clear();
        board.addAll(startConfiguration);
        movesMade = 0;
        hint = "reset";
        setState(PuzzleState.IN_PROGRESS);
    }

    /** Prints a simple console view of the board for quick checks. */
    public void displayPuzzle() {
        for (int r = 0; r < rows; r++) {
            int s = r * cols, e = s + cols;
            System.out.println(board.subList(s, e));
        }
    }

    // -------- difficulty wiring --------

    /** Sets the difficulty level and applies any related move caps. */
    public void setDifficulty(Difficulty d) {
        this.difficulty = d; // can be null
        applyDifficulty();
    }

    /** Applies internal difficulty rules such as move limits. */
    private void applyDifficulty() {
        if (difficulty == null) {
            maxMoves = Integer.MAX_VALUE; // default: no cap
            return;
        }
        String level = difficulty.toString();
        level = (level == null) ? "" : level.trim().toUpperCase();

        switch (level) {
            case "HARD" -> maxMoves = 60;            // simple cap for harder mode
            case "EASY" -> maxMoves = Integer.MAX_VALUE;
            default     -> maxMoves = Integer.MAX_VALUE; // MEDIUM/unknown -> no cap
        }
    }

    // -------- core slide logic --------

    /** Attempts to slide a tile into the empty slot in the specified direction. */
    private boolean slide(String tile, String dir) {
        if (tile == null || dir == null || "_".equals(tile)) return false;

        int emptyIdx = board.indexOf("_");
        int tileIdx  = board.indexOf(tile);
        if (emptyIdx < 0 || tileIdx < 0) return false;

        int er = emptyIdx / cols, ec = emptyIdx % cols;
        int tr = tileIdx  / cols, tc = tileIdx  % cols;

        switch (dir.toLowerCase()) {
            case "up":    if (tr - 1 == er && tc == ec) return swap(tileIdx, emptyIdx); break;
            case "down":  if (tr + 1 == er && tc == ec) return swap(tileIdx, emptyIdx); break;
            case "left":  if (tr == er && tc - 1 == ec) return swap(tileIdx, emptyIdx); break;
            case "right": if (tr == er && tc + 1 == ec) return swap(tileIdx, emptyIdx); break;
            default: hint = "direction must be up/down/left/right"; return false;
        }
        hint = "that tile isn't next to the empty in that direction";
        return false;
    }

    /** Swaps the tile and empty slot positions and increments the move counter. */
    private boolean swap(int a, int b) {
        java.util.Collections.swap(board, a, b);
        movesMade++;
        hint = "moved";

        // enforce simple move cap only for hard-like difficulty
        if (movesMade > maxMoves && !isSolved()) {
            hint = "move limit reached for difficulty";
            // we don't mark FAILED to avoid breaking flows; just warn
        }
        return true;
    }

    // -------- required by Puzzle --------

    /**
     * Parses user input (e.g., "A up", "slide B left", "solved", "reset") and applies the move or command.
     * Updates puzzle state and returns a {@link ValidationResult} with feedback.
     */
    @Override
    public ValidationResult enterInput(String s) {
        if (s == null || s.isBlank()) {
            hint = "give input like: A up";
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.invalidFormat(hint, PuzzleState.IN_PROGRESS);
        }

        if (movesMade > maxMoves && !isSolved()) {
            hint = "move limit reached for difficulty";
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.invalidFormat(hint, PuzzleState.IN_PROGRESS);
        }

        String in = s.trim();
        if ("reset".equalsIgnoreCase(in)) {
            resetToStart();
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.invalidFormat("reset", PuzzleState.IN_PROGRESS);
        }

        if ("solved".equalsIgnoreCase(in)) {
            if (isSolved()) {
                setState(PuzzleState.SOLVED);
                return ValidationResult.valid("Puzzle solved!", PuzzleState.SOLVED);
            }
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.invalidFormat("Not solved yet", PuzzleState.IN_PROGRESS);
        }

        String[] parts = in.split("\\s+");
        String tile;
        String dir;
        if (parts.length == 3 && parts[0].equalsIgnoreCase("slide")) {
            tile = parts[1].toUpperCase();
            dir  = parts[2];
        } else if (parts.length == 2) {
            tile = parts[0].toUpperCase();
            dir  = parts[1];
        } else {
            hint = "format: slide <tile> <dir>";
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.invalidFormat(hint, PuzzleState.IN_PROGRESS);
        }

        if (!board.contains(tile)) {
            hint = "unknown tile: " + tile;
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.invalidFormat(hint, PuzzleState.IN_PROGRESS);
        }
        String d = dir.toLowerCase();
        if (!(d.equals("up") || d.equals("down") || d.equals("left") || d.equals("right"))) {
            hint = "direction must be up/down/left/right";
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.invalidFormat(hint, PuzzleState.IN_PROGRESS);
        }

        boolean moved = slide(tile, d);
        if (moved) {
            if (isSolved()) {
                setState(PuzzleState.SOLVED);
                return ValidationResult.valid("Solved in " + movesMade + " moves.", PuzzleState.SOLVED);
            }
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.invalidFormat("Moved " + tile + " " + d, PuzzleState.IN_PROGRESS);
        } else {
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.invalidFormat(hint, PuzzleState.IN_PROGRESS);
        }
    }

    /** Checks a simple achievement condition: returns true only if the puzzle is solved. */
    @Override
    public boolean checkSpecificAchievementCondition(Achievement achievement,
                                                     java.time.Duration timeTaken,
                                                     int moves,
                                                     int extra) {
        // Keep it simple for now: unlock only if solved.
        return isSolved();
    }

    /** Returns a brief string representation of the puzzle state. */
    @Override
    public String toString() {
        return "SlideShapePuzzle{moves=" + movesMade + ", board=" + board + "}";
    }
}
