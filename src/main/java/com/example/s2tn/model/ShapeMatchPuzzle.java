package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Puzzle where user must place shapes to match target poses within a tolerance. */
public class ShapeMatchPuzzle extends Puzzle {

    private List<String> targetShapes;   // e.g., ["A:10,20,0", "B:30,40,90"]
    private double tolerance;            // pixels/deg tolerance

    // ---- constructors ----
    public ShapeMatchPuzzle(List<String> targetShapes, double tolerance) {
        this.targetShapes = new ArrayList<>(targetShapes == null ? List.of() : targetShapes);
        this.tolerance = tolerance;
    }

    public ShapeMatchPuzzle() {
        this.targetShapes = new ArrayList<>();
        this.tolerance = 0.0;
    }

    // ---- getters / setters ----
    public List<String> getTargetShapes() {
        return Collections.unmodifiableList(targetShapes);
    }

    public void setTargetShapes(List<String> targetShapes) {
        this.targetShapes = new ArrayList<>(targetShapes == null ? List.of() : targetShapes);
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    // ---- tiny model for a shape pose ----
    private static final class ShapePose {
        final String id;
        final double x, y, rot;

        ShapePose(String id, double x, double y, double rot) {
            this.id = id == null ? "" : id.trim();
            this.x = x;
            this.y = y;
            this.rot = rot;
        }

        /** Accepts: "id:x,y,rot" | "id:x,y" | "id" */
        static ShapePose parse(String raw) {
            if (raw == null || raw.isBlank()) return new ShapePose("", 0, 0, 0);
            String s = raw.trim();
            String[] idAndRest = s.split(":", 2);
            String id = idAndRest[0].trim();
            double x = 0, y = 0, r = 0;
            if (idAndRest.length == 2) {
                String[] parts = idAndRest[1].split(",");
                try { if (parts.length > 0) x = Double.parseDouble(parts[0].trim()); } catch (Exception ignore) {}
                try { if (parts.length > 1) y = Double.parseDouble(parts[1].trim()); } catch (Exception ignore) {}
                try { if (parts.length > 2) r = Double.parseDouble(parts[2].trim()); } catch (Exception ignore) {}
            }
            return new ShapePose(id, x, y, r);
        }
    }

    private List<ShapePose> toPoses(List<String> raw) {
        List<ShapePose> out = new ArrayList<>();
        if (raw == null) return out;
        for (String s : raw) out.add(ShapePose.parse(s));
        return out;
    }

    private static boolean withinTol(ShapePose a, ShapePose b, double tol) {
        return Math.abs(a.x - b.x) <= tol
            && Math.abs(a.y - b.y) <= tol
            && Math.abs(a.rot - b.rot) <= tol;
    }

    /** True if every target id has a user pose within tolerance (size must match). */
    private boolean aligned(List<String> userShapesRaw) {
        List<ShapePose> targets = toPoses(this.targetShapes);
        List<ShapePose> users   = toPoses(userShapesRaw);
        if (targets.size() != users.size()) return false;

        Map<String, ShapePose> byId = new HashMap<>();
        for (ShapePose u : users) byId.put(u.id, u);

        for (ShapePose t : targets) {
            ShapePose u = byId.get(t.id);
            if (u == null || !withinTol(t, u, this.tolerance)) return false;
        }
        return true;
    }

    /** Hook for text input if UI sends it; noop for now. */
    @Override
    public void enterInput(String input) {
        if (input == null || input.isBlank()) return;
        // could parse and update a user-pose map here later if needed
    }

    /** Provided for callers that want to “apply” alignment; currently stateless. */
    public void align(List<String> shapes) {
        // no state kept here; alignment is evaluated via isSolved()
    }

    /** Public solved check for the puzzle engine. */
    public boolean isSolved(List<String> userShapes) {
        return aligned(userShapes);
    }
}
