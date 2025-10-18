package com.example.s2tn.model;

public enum Difficulty {
    EASY(0, true, 0),      // unlimited moves, undo allowed, instant hints
    NORMAL(40, true, 3),   // 40 moves, undo allowed, 3-move hint cooldown
    HARD(25, false, 5);    // 25 moves, no undo, 5-move hint cooldown

    private final int maxMoves;
    private final boolean allowUndo;
    private final int hintCooldown;

    Difficulty(int maxMoves, boolean allowUndo, int hintCooldown) {
        this.maxMoves = maxMoves;
        this.allowUndo = allowUndo;
        this.hintCooldown = hintCooldown;
    }

    public int getMaxMoves() { return maxMoves; }
    public boolean isUndoAllowed() { return allowUndo; }
    public int getHintCooldown() { return hintCooldown; }
}
