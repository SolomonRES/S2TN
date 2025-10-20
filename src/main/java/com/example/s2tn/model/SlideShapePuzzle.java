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
    }

    /** simple default 3x3 */
    public SlideShapePuzzle() {
        this(
            java.util.Arrays.asList("A","B","C","D","E","F","G","_","H"),
            java.util.Arrays.asList("A","B","C","D","E","F","G","H","_"),
            3, 3
        );
    }

    // -------- basics --------

    public java.util.List<String> getBoard() { return new java.util.ArrayList<>(board); }
    public int getMovesMade() { return movesMade; }
    public String getHint() { return hint; }
    public boolean isSolved() { return board.equals(endConfiguration); }

    /** reset board back to the original start config */
    public void resetToStart() {
        board.clear();
        board.addAll(startConfiguration);
        movesMade = 0;
        hint = "reset";
        setState(PuzzleState.IN_PROGRESS);
    }

    /** tiny console display for quick checks */
    public void displayPuzzle() {
        for (int r = 0; r < rows; r++) {
            int s = r * cols, e = s + cols;
            System.out.println(board.subList(s, e));
        }
    }

    // -------- core slide logic --------

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

    private boolean swap(int a, int b) {
        java.util.Collections.swap(board, a, b);
        movesMade++;
        hint = "moved";
        return true;
    }

    // -------- required by Puzzle --------

    @Override
    public ValidationResult enterInput(String s) {
        if (s == null || s.isBlank()) {
            hint = "give input like: A up";
            setState(PuzzleState.IN_PROGRESS);
            return new ValidationResult(false, hint, PuzzleState.IN_PROGRESS);
        }

        String in = s.trim();
        if ("solved".equalsIgnoreCase(in)) {
            if (isSolved()) {
                setState(PuzzleState.SOLVED);
                return new ValidationResult(true, "Puzzle solved!", PuzzleState.SOLVED);
            }
            setState(PuzzleState.IN_PROGRESS);
            return new ValidationResult(false, "Not solved yet", PuzzleState.IN_PROGRESS);
        }

        // accept "slide A up" or "A up"
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
            return new ValidationResult(false, hint, PuzzleState.IN_PROGRESS);
        }

        boolean moved = slide(tile, dir);
        if (moved) {
            if (isSolved()) {
                setState(PuzzleState.SOLVED);
                return new ValidationResult(true, "Solved in " + movesMade + " moves.", PuzzleState.SOLVED);
            }
            setState(PuzzleState.IN_PROGRESS);
            return new ValidationResult(false, "Moved " + tile + " " + dir, PuzzleState.IN_PROGRESS);
        } else {
            setState(PuzzleState.IN_PROGRESS);
            return new ValidationResult(false, hint, PuzzleState.IN_PROGRESS);
        }
    }

    /** required by the current Puzzle abstract class */
    @Override
    public boolean checkSpecificAchievementCondition(Achievement achievement,
                                                     java.time.Duration timeTaken,
                                                     int moves,
                                                     int extra) {
        // Keep it simple for now: unlock only if solved.
        return isSolved();
    }

    @Override
    public String toString() {
        return "SlideShapePuzzle{moves=" + movesMade + ", board=" + board + "}";
    }
}
