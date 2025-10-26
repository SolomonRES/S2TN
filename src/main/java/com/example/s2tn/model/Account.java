package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a user account in the system.
 */
public class Account {

    private UUID accountID;
    private String userName;
    private String password;
    private int score;
    private int rank;
    private List<Achievement> achievements;

    /** Creates an empty account with default values. */
    public Account() {
        this.accountID = UUID.randomUUID();
        this.userName = "";
        this.password = "";
        this.score = 0;
        this.rank = 0;
        this.achievements = new ArrayList<>();
    }

    /** Creates an account with a username and password. */
    public Account(String userName, String password) {
        this.accountID = UUID.randomUUID();
        this.userName = userName == null ? "" : userName.trim();
        this.password = password == null ? "" : password.trim();
        this.score = 0;
        this.rank = 0;
        this.achievements = new ArrayList<>();
    }

    /** Returns the account ID as a string. */
    public String getAccountID() {
        return accountID == null ? "" : accountID.toString();
    }

    /** Sets the account ID from a string (generates a new one if invalid). */
    public void setAccountID(String id) {
        try {
            this.accountID = UUID.fromString(id);
        } catch (Exception e) {
            this.accountID = UUID.randomUUID();
        }
    }

    /** Checks if the provided username and password match this account. */
    public boolean login(String user, String pass) {
        if (user == null || pass == null) return false;
        if (this.userName == null || this.password == null) return false;
        if (!this.userName.equalsIgnoreCase(user)) return false;
        return pass.equals(this.password);
    }

    /** Updates the user's score by adding or subtracting points. */
    public void updateScore(int points) {
        if (points == 0) return;
        long updated = (long) this.score + points;
        this.score = (int) Math.max(0, Math.min(Integer.MAX_VALUE, updated));
    }

    /** Adds an achievement if it’s not already earned. */
    public void addAchievements(Achievement a) {
        if (a == null) return;
        if (achievements == null) achievements = new ArrayList<>();
        if (!achievements.contains(a)) achievements.add(a);
    }

    /** Returns a copy of the user's achievements. */
    public List<Achievement> getAchievements() {
        return new ArrayList<>(achievements);
    }

    /** Returns the username. */
    public String getUserName() {
        return userName;
    }

    /** Sets the username. */
    public void setUserName(String userName) {
        this.userName = userName == null ? "" : userName.trim();
    }

    /** Returns the password. */
    public String getPassword() {
        return password;
    }

    /** Sets the password. */
    public void setPassword(String password) {
        this.password = password == null ? "" : password.trim();
    }

    /** Returns the current score. */
    public int getScore() {
        return score;
    }

    /** Sets the score (must be non-negative). */
    public void setScore(int score) {
        this.score = Math.max(0, score);
    }

    /** Returns the user's rank. */
    public int getRank() {
        return rank;
    }

    /** Sets the user's rank (must be non-negative). */
    public void setRank(int rank) {
        this.rank = Math.max(0, rank);
    }
}
