package com.s2tn.model;

/**
 * Represents a hint provided to the player, with an associated level, text, and time cost.
 */
public class Hint {

    private int level;
    private String text;
    private double cost; // cost in time for the user

    /** Creates a default hint with no text and zero cost. */
    public Hint() {
        this.level = 0;
        this.text = "";
        this.cost = 0.0; // tester until we get hints for mazes
    }

    /** Creates a hint with a specific level, text, and cost. */
    public Hint(int level, String text, double cost) {
        this.level = level;
        this.text = text == null ? "" : text;
        this.cost = cost;
    }

    /** Returns the hint text. */
    public String getHint() {
        return this.text;
    }

    /** Returns the hint level. */
    public int getLevel() {
        return level;
    }

    /** Sets the hint level. */
    public void setLevel(int level) {
        this.level = level;
    }

    /** Returns the hint text. */
    public String getText() {
        return text;
    }

    /** Sets the hint text. */
    public void setText(String text) {
        this.text = text;
    }

    /** Returns the time cost of using this hint. */
    public double getCost() {
        return cost;
    }

    /** Sets the time cost for this hint. */
    public void setCost(double cost) {
        this.cost = cost;
    }

    /** Returns a string representation of the hint. */
    @Override
    public String toString() {
        return "Hint(level=" + level + ", cost=" + cost + ", text=\"" + text + "\")";
    }
}
