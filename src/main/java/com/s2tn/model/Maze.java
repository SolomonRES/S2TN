package com.s2tn.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Maze puzzle where the player moves from start to end.
 * - Keeps string-facing API (startPosition/endPosition/playerPosition)
 * - Real movement (U/D/L/R, WASD, UP/DOWN/LEFT/RIGHT) + "r,c" absolute
 * - Bounds + optional walls
 * - Uses ValidationResult with PuzzleState; no direct state setter calls
 */
public class Maze extends Puzzle {

    // string-facing API (per UML)
    private String startPosition;
    private String endPosition;
    private String playerPosition;
    private String hint;

    // grid model
    private int rows = 0;
    private int cols = 0;
    private int startR = 0, startC = 0;
    private int endR   = 0, endC   = 0;
    private int r = 0, c = 0; // player (row, col)

    // walls
    private static final class Cell {
        final int r, c;
        Cell(int r, int c) { this.r = r; this.c = c; }
        @Override public boolean equals(Object o) {
            if (!(o instanceof Cell)) return false;
            Cell other = (Cell) o;
            return r == other.r && c == other.c;
        }
        @Override public int hashCode() { return Objects.hash(r, c); }
    }
    private final Set<Cell> walls = new HashSet<Cell>();

    public Maze() {
        this.startPosition  = "";
        this.endPosition    = "";
        this.playerPosition = "";
        this.hint           = "Set grid and end first";
    }

    // ---------- setup / accessors ----------
    public void setStartPosition(String pos) {
        this.startPosition = (pos == null) ? "" : pos.trim();
        if (!this.startPosition.trim().isEmpty() && rows > 0 && cols > 0) {
            int[] s = parsePos(this.startPosition);
            this.startR = s[0]; this.startC = s[1];
            if (playerPosition.trim().isEmpty()) {
                this.r = startR; this.c = startC;
                this.playerPosition = formatPos(r, c);
            }
        }
        refreshHint();
    }

    public void setEndPosition(String pos) {
        this.endPosition = (pos == null) ? "" : pos.trim();
        if (!this.endPosition.trim().isEmpty()) {
            int[] e = parsePos(this.endPosition);
            this.endR = e[0]; this.endC = e[1];
        }
        refreshHint();
    }

    public void setPlayerPosition(String pos) {
        this.playerPosition = (pos == null) ? "" : pos.trim();
        if (!this.playerPosition.trim().isEmpty()) {
            int[] p = parsePos(this.playerPosition);
            this.r = p[0]; this.c = p[1];
        }
        refreshHint();
    }

    public String getStartPosition()  { return this.startPosition; }
    public String getEndPosition()    { return this.endPosition; }
    public String getPlayerPosition() { return this.playerPosition; }

    /** Configure grid and main cells. */
    public void setGrid(int rows, int cols, String start, String end) {
        if (rows <= 0 || cols <= 0) throw new IllegalArgumentException("rows/cols > 0");
        this.rows = rows; this.cols = cols;

        int[] s = parsePos(start);
        int[] e = parsePos(end);
        this.startR = s[0]; this.startC = s[1];
        this.endR   = e[0]; this.endC   = e[1];

        this.r = startR; this.c = startC;

        // keep string API in sync
        this.startPosition  = formatPos(startR, startC);
        this.endPosition    = formatPos(endR, endC);
        this.playerPosition = formatPos(r, c);

        refreshHint();
    }

    public void addWall(int wr, int wc) { walls.add(new Cell(wr, wc)); }
    public void clearWalls() { walls.clear(); }

    // console helper
    public void displayPuzzle() {
        System.out.println("Maze: Player=" + playerPosition + " grid " + rows + "x" + cols);
    }

    /** Back-compat only; not used for real movement. */
    public String move(String direction) {
        if (direction == null || direction.trim().isEmpty()) return "invalid";
        playerPosition += direction.trim().toUpperCase().charAt(0);
        refreshHint();
        return playerPosition;
    }

    public boolean checkAnswer(String userInput) {
        if (userInput == null) return false;
        return userInput.trim().equalsIgnoreCase(endPosition);
    }

    // ---------- required API ----------
    // (no @Override in case base signature differs)
    public String getHint() {
        String base = (hint == null) ? "" : hint;
        if (rows > 0 && cols > 0 && endPosition != null && !endPosition.trim().isEmpty()) {
            int dist = Math.abs(endR - r) + Math.abs(endC - c);
            return base + " | distance≈" + dist;
        }
        return base;
    }

    @Override
    public ValidationResult enterInput(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            hint = "Empty input";
            return vr(false, "Empty input", PuzzleState.IN_PROGRESS);
        }

        String t = userInput.trim().toUpperCase();

        // 1) Try direction
        String dir = normalizeDirection(t);
        if (dir != null) {
            boolean moved = tryMove(dir);
            if (!moved) {
                hint = "Blocked (wall or edge). Try another direction.";
                syncStrings();
                return vr(false, "blocked", PuzzleState.IN_PROGRESS);
            }
            if (r == endR && c == endC) {
                hint = "You're at the end!";
                syncStrings();
                return vr(true, "reached goal", PuzzleState.SOLVED);
            }
            hint = "Moved " + dir + ". Keep going.";
            syncStrings();
            return vr(true, "ok", PuzzleState.IN_PROGRESS);
        }

        // 2) Else try absolute coordinate "r,c"
        try {
            int[] p = parsePos(userInput);
            if (!inBounds(p[0], p[1]) || isBlocked(p[0], p[1])) {
                hint = "Invalid cell (out of bounds or wall).";
                return vr(false, "invalid cell", PuzzleState.IN_PROGRESS);
            }
            r = p[0]; c = p[1];
            if (r == endR && c == endC) {
                hint = "You're at the end!";
                syncStrings();
                return vr(true, "reached goal", PuzzleState.SOLVED);
            }
            hint = "Position updated to " + formatPos(r, c);
            syncStrings();
            return vr(true, "position set", PuzzleState.IN_PROGRESS);
        } catch (Exception ignore) {
            hint = "Use U/D/L/R (or WASD/words) or 'r,c'.";
            return vr(false, "bad input", PuzzleState.IN_PROGRESS);
        }
    }

    // Base requires this; keep simple for now.
    @Override
    public boolean checkSpecificAchievementCondition(
            Achievement achievement,
            java.time.Duration elapsed,
            int movesMade,
            int hintsUsed) {
        return false;
    }

    // ---------- helpers ----------
    private void refreshHint() {
        if (endPosition == null || endPosition.isEmpty()) {
            hint = "Set grid and end first";
        } else if (playerPosition.equalsIgnoreCase(endPosition)) {
            hint = "You're at the end!";
        } else {
            hint = "Keep moving toward " + endPosition;
        }
    }

    private void syncStrings() {
        this.playerPosition = formatPos(r, c);
        if (this.startPosition == null || this.startPosition.trim().isEmpty()) {
            this.startPosition = formatPos(startR, startC);
        }
        if (this.endPosition == null || this.endPosition.trim().isEmpty()) {
            this.endPosition = formatPos(endR, endC);
        }
    }

    private String normalizeDirection(String t) {
        if (t.equals("U") || t.equals("UP") || t.equals("W")) return "U";
        if (t.equals("D") || t.equals("DOWN") || t.equals("S")) return "D";
        if (t.equals("L") || t.equals("LEFT") || t.equals("A")) return "L";
        if (t.equals("R") || t.equals("RIGHT") || t.equals("R")) return "R";
        if (t.startsWith("UP")) return "U";
        if (t.startsWith("DOWN")) return "D";
        if (t.startsWith("LEFT")) return "L";
        if (t.startsWith("RIGHT")) return "R";
        return null;
    }

    private boolean tryMove(String dir) {
        int nr = r, nc = c;
        if ("U".equals(dir)) nr = r - 1;
        else if ("D".equals(dir)) nr = r + 1;
        else if ("L".equals(dir)) nc = c - 1;
        else if ("R".equals(dir)) nc = c + 1;

        if (!inBounds(nr, nc) || isBlocked(nr, nc)) return false;
        r = nr; c = nc;
        return true;
    }

    private boolean inBounds(int rr, int cc) {
        return rr >= 0 && rr < rows && cc >= 0 && cc < cols;
    }

    private boolean isBlocked(int rr, int cc) {
        return walls.contains(new Cell(rr, cc));
    }

    private int[] parsePos(String pos) {
        if (pos == null || pos.trim().isEmpty()) throw new IllegalArgumentException("pos required");
        String[] parts = pos.trim().split("\\s*,\\s*");
        if (parts.length != 2) throw new IllegalArgumentException("use 'r,c'");
        int rr = Integer.parseInt(parts[0]);
        int cc = Integer.parseInt(parts[1]);
        return new int[]{ rr, cc };
    }

    private String formatPos(int rr, int cc) { return rr + "," + cc; }

    public void reset() {
        this.r = startR; this.c = startC;
        this.playerPosition = formatPos(r, c);
        hint = "reset";
    }

    // ValidationResult compatibility (factory or ctor)
    private ValidationResult vr(boolean ok, String msg, PuzzleState state) {
        try {
            java.lang.reflect.Method m =
                    ValidationResult.class.getDeclaredMethod(ok ? "valid" : "invalid",
                            String.class, PuzzleState.class);
            return (ValidationResult) m.invoke(null, msg, state);
        } catch (Throwable ignore) {
            try {
                java.lang.reflect.Constructor<ValidationResult> ctor =
                        ValidationResult.class.getDeclaredConstructor(
                                boolean.class, String.class, PuzzleState.class);
                ctor.setAccessible(true);
                return ctor.newInstance(ok, msg, state);
            } catch (Throwable e) {
                throw new RuntimeException("ValidationResult API mismatch", e);
            }
        }
    }
}
