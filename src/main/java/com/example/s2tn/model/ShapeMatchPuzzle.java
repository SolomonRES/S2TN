package com.example.s2tn.model;

import java.time.Duration;

// user places shapes near target spots; keep it simple
public class ShapeMatchPuzzle extends Puzzle {

    // fully qualify to avoid clash with com.example.s2tn.model.Map
    private final java.util.Map<String, Pose> target = new java.util.HashMap<>();
    private final java.util.Map<String, Pose> placed = new java.util.HashMap<>();

    private int tolPx = 8;   // pixels
    private int tolDeg = 10; // degrees
    private String hint = "";

    // we won't assume enum constants exist; null means "default/medium-like"
    private Difficulty difficulty;

    public ShapeMatchPuzzle() {
        applyDifficulty(); // keep existing defaults if difficulty is null
        setState(PuzzleState.IN_PROGRESS);
    }

    // quick reset for UX (keeps targets; clears placements)
    public void resetToStart() {
        placed.clear();
        movesHint("reset");
        setState(PuzzleState.IN_PROGRESS);
    }

    // setup: e.g. setTarget("A", 10, 20, 0)
    public void setTarget(String id, int x, int y, int deg) {
        target.put(id, new Pose(x, y, normDeg(deg)));
        placed.putIfAbsent(id, null);
    }

    // player action
    public void place(String id, int x, int y, int deg) {
        if (!target.containsKey(id)) {
            movesHint("No such shape");
            return;
        }
        placed.put(id, new Pose(x, y, normDeg(deg)));
        movesHint(id + " placed");
    }

    // ONLY abstract in Puzzle: must return ValidationResult
    @Override
    public ValidationResult enterInput(String s) {
        // accept "A:10,20,0"
        if (s == null) {
            movesHint("no input");
            setState(PuzzleState.IN_PROGRESS);
            return new ValidationResult(false, hint, PuzzleState.IN_PROGRESS);
        }

        String in = s.trim();
        if (in.isEmpty()) {
            movesHint("no input");
            setState(PuzzleState.IN_PROGRESS);
            return new ValidationResult(false, hint, PuzzleState.IN_PROGRESS);
        }

        // allow a simple reset command
        if (in.equalsIgnoreCase("reset")) {
            resetToStart();
            return new ValidationResult(false, "reset", PuzzleState.IN_PROGRESS);
        }

        int colon = in.indexOf(':');
        if (colon <= 0 || colon == in.length() - 1) {
            movesHint("use id:x,y,deg");
            setState(PuzzleState.IN_PROGRESS);
            return new ValidationResult(false, hint, PuzzleState.IN_PROGRESS);
        }

        String id = in.substring(0, colon).trim();
        String numsPart = in.substring(colon + 1).trim();

        if (!target.containsKey(id)) {
            movesHint("unknown id: " + id);
            setState(PuzzleState.IN_PROGRESS);
            return new ValidationResult(false, hint, PuzzleState.IN_PROGRESS);
        }

        String[] nums = numsPart.split(",");
        if (nums.length < 3) {
            movesHint("need 3 numbers");
            setState(PuzzleState.IN_PROGRESS);
            return new ValidationResult(false, hint, PuzzleState.IN_PROGRESS);
        }

        try {
            int x = Integer.parseInt(nums[0].trim());
            int y = Integer.parseInt(nums[1].trim());
            int d = Integer.parseInt(nums[2].trim());

            // keep degree sane (0..359)
            d = normDeg(d);

            place(id, x, y, d);
        } catch (NumberFormatException e) {
            movesHint("bad numbers");
            setState(PuzzleState.IN_PROGRESS);
            return new ValidationResult(false, hint, PuzzleState.IN_PROGRESS);
        }

        boolean solved = checkAnswer(""); // just re-check after placing
        if (solved) {
            setState(PuzzleState.SOLVED);
            return new ValidationResult(true, "All shapes match!", PuzzleState.SOLVED);
        } else {
            setState(PuzzleState.IN_PROGRESS);
            return new ValidationResult(false, hint, PuzzleState.IN_PROGRESS);
        }
    }

    // required by Puzzle now
    @Override
    public boolean checkSpecificAchievementCondition(Achievement achievement,
                                                     Duration duration,
                                                     int hintsUsed,
                                                     int currentScore) {
        // keep it simple: only award if solved
        return checkAnswer("");
    }

    // helper display (not abstract in Puzzle)
    public void displayPuzzle() {
        System.out.println("-- ShapeMatchPuzzle --");
        System.out.println("targets: " + target);
        System.out.println("placed : " + placed);
    }

    // simple solved check (not abstract in Puzzle)
    public boolean checkAnswer(String userInput) {
        for (java.util.Map.Entry<String, Pose> e : target.entrySet()) {
            String id = e.getKey();
            Pose t = e.getValue();
            Pose p = placed.get(id);

            if (p == null) {
                movesHint("Place " + id);
                return false;
            }

            int dx = Math.abs(t.x - p.x);
            int dy = Math.abs(t.y - p.y);
            int dd = Math.abs(t.deg - p.deg);

            if (dx > tolPx || dy > tolPx || dd > tolDeg) {
                movesHint(id + " off by (" + dx + "," + dy + "," + dd + ")");
                return false;
            }
        }
        movesHint("All shapes match!");
        return true;
    }

    public String getHint() { return hint; }

    // --- Difficulty wiring (no dependency on enum constants) ---

    public void setDifficulty(Difficulty d) {
        this.difficulty = d; // may be null; that's fine (defaults apply)
        applyDifficulty();
    }

    private void applyDifficulty() {
        if (difficulty == null) {
            // default (medium-like)
            tolPx = 8;
            tolDeg = 10;
            return;
        }

        // Compare by name/text, so it works whether Difficulty is enum or class.
        String level = difficulty.toString();
        level = (level == null) ? "" : level.trim().toUpperCase();

        switch (level) {
            case "EASY" -> { tolPx = 12; tolDeg = 15; }
            case "HARD" -> { tolPx = 4;  tolDeg = 5;  }
            default     -> { tolPx = 8;  tolDeg = 10; } // MEDIUM or anything else
        }
    }

    // helpers
    private static int normDeg(int d) {
        int r = d % 360;
        return (r < 0) ? r + 360 : r;
    }

    private void movesHint(String h) {
        this.hint = h;
    }

    // tiny holder for a pose
    private static class Pose {
        int x, y, deg;
        Pose(int x, int y, int deg) { this.x = x; this.y = y; this.deg = deg; }
        public String toString() { return x + "," + y + "," + deg; }
    }
}
