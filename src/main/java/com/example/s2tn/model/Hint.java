package com.example.s2tn.model;

public class Hint {

    private int level;
    private String text;
    private double cost; // cost in time for the user

    public Hint() {
        this.level = 0;
        this.text = "";
        this.cost = 0.0; // tester until we get hints for mazes
    }

    public Hint(int level, String text, double cost) {
        this.level = level;
        this.text = text == null ? "" : text;
        this.cost = cost;
    }

    // returns the hint text
    public String getHint() {
        return this.text;
    }

    // getters/setters
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Hint(level=" + level + ", cost=" + cost + ", text=\"" + text + "\")";
    }
}