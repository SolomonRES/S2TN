package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShapeMatchPuzzle {

    private List<String> targetShapes;
    private double tolerance;

    /** Basic constructor used by the game when loading a puzzle config */
    public ShapeMatchPuzzle(List<String> targetShapes, double tolerance) {
        this.targetShapes = new ArrayList<>(targetShapes == null ? List.of() : targetShapes);
        this.tolerance = tolerance;
    }

    /** Convenience empty constructor (useful for DataLoader or tests) */
    public ShapeMatchPuzzle() {
        this.targetShapes = new ArrayList<>();
        this.tolerance = 0.0;
    }

    // --- getters / setters ---
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

    /** Align user shapes to the puzzle's target (logic comes next step). */
    public void align(List<String> shapes) {
        // TODO: Step 5 will implement actual alignment logic.
        if (shapes == null) return;
        // no-op for now so we keep builds green
    }

    /** Temporary “is solved” check (order-insensitive exact match). */
    public boolean isSolved(List<String> userShapes) {
        if (userShapes == null || userShapes.size() != targetShapes.size()) return false;
        return userShapes.containsAll(targetShapes) && targetShapes.containsAll(userShapes);
    }
}
