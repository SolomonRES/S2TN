package com.example.s2tn.model;

import java.util.HashMap;
import java.util.Map;

/** Align labeled shapes to target poses within a tolerance. */
public class ShapeMatchPuzzle extends Puzzle {

    private static final double DEFAULT_POS_TOL = 5.0; // pixels
    private static final double DEFAULT_ANG_TOL = 5.0; // degrees

    private final Map<String, Pose> targets = new HashMap<>();
    private double posTol = DEFAULT_POS_TOL;
    private double angTol = DEFAULT_ANG_TOL;

    private String lastHint = "";

    private static class Pose {
        final double x, y, deg;
        Pose(double x, double y, double deg) { this.x = x; this.y = y; this.deg = deg; }
    }

    /** Define/overwrite a target pose for a shape label (e.g., "A"). */
    public void setTarget(String label, double x, double y, double deg) {
        if (label == null || label.isBlank()) return;
        targets.put(label.trim(), new Pose(x, y, deg));
    }

    /** Optional: tweak tolerances (pixels, degrees). Must be positive to apply. */
    public void setTolerance(double positionPixels, double angleDegrees) {
        if (positionPixels > 0) this.posTol = positionPixels;
        if (angleDegrees   > 0) this.angTol = angleDegrees;
    }

    /** Latest hint after enterInput(). */
    public String getHint() { return lastHint; }

    /**
     * Input format: "A:10,20,0; B:30,40,90"
     * Returns valid=true only if ALL targets are within tolerance.
     */
    @Override
    public ValidationResult enterInput(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            lastHint = "no input";
            return new ValidationResult(false, "Empty input", PuzzleState.IN_PROGRESS);
        }
        if (targets.isEmpty()) {
            lastHint = "no targets";
            return new ValidationResult(false, "No targets set", PuzzleState.IN_PROGRESS);
        }

        Map<String, Pose> observed = parse(userInput);
        if (observed.isEmpty()) {
            lastHint = "bad format (use A:x,y,deg; B:x,y,deg)";
            return new ValidationResult(false, "Bad format", PuzzleState.IN_PROGRESS);
        }

        for (Map.Entry<String, Pose> e : targets.entrySet()) {
            String lbl = e.getKey();
            Pose tgt = e.getValue();
            Pose got = observed.get(lbl);
            if (got == null) {
                lastHint = "missing shape: " + lbl;
                return new ValidationResult(false, "Missing " + lbl, PuzzleState.IN_PROGRESS);
            }

            double dx = got.x - tgt.x;
            double dy = got.y - tgt.y;
            double dist = Math.hypot(dx, dy);
            double dAng = angleDelta(got.deg, tgt.deg);

            if (dist > posTol) {
                lastHint = lbl + " off by " + round1(dist) + "px";
                return new ValidationResult(false, lastHint, PuzzleState.IN_PROGRESS);
            }
            if (Math.abs(dAng) > angTol) {
                lastHint = lbl + " angle off by " + round1(Math.abs(dAng)) + "°";
                return new ValidationResult(false, lastHint, PuzzleState.IN_PROGRESS);
            }
        }

        lastHint = "aligned";
        return new ValidationResult(true, "Solved", PuzzleState.SOLVED);
    }

    // ---- helpers ----

    private static double angleDelta(double a, double b) {
        double d = (a - b) % 360.0;
        if (d < -180) d += 360;
        if (d > 180)  d -= 360;
        return d;
    }

    private static String round1(double v) {
        return String.format("%.1f", v);
    }

    private static Map<String, Pose> parse(String s) {
        Map<String, Pose> out = new HashMap<>();
        for (String part : s.split(";")) {
            String piece = part.trim();
            if (piece.isEmpty()) continue;

            int colon = piece.indexOf(':');
            if (colon <= 0 || colon == piece.length() - 1) continue;

            String label = piece.substring(0, colon).trim();
            String nums = piece.substring(colon + 1).trim();
            String[] xyz = nums.split(",");
            if (xyz.length != 3) continue;

            try {
                double x = Double.parseDouble(xyz[0].trim());
                double y = Double.parseDouble(xyz[1].trim());
                double d = Double.parseDouble(xyz[2].trim());
                out.put(label, new Pose(x, y, d));
            } catch (NumberFormatException ignored) { /* skip bad piece */ }
        }
        return out;
    }
    @Override
public boolean checkSpecificAchievementCondition(
        Achievement achievement,
        java.time.Duration elapsed,
        int movesMade,
        int hintsUsed) {
    // No special per-puzzle achievements for ShapeMatch yet.
    // Defer to generic checks handled elsewhere.
    return false;
}

}
