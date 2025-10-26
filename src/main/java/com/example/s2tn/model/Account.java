package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a user account in the system.
 * Each account has a unique ID, username, password, score, rank, and a list of achievements.
 */
public class Account {

    /** Unique ID for the account */
    private UUID accountID;

    /** Username of the account */
    private String userName;

    /** Password of the account */
    private String password;

    /** Total score for the user */
    private int score;

    /** Rank of the user */
    private int rank;

    /** List of achievements earned by the user */
    private List<Achievement> achievements;

    /**
     * Default constructor.
     * Creates an empty account with default values.
     */
    public Account() {
        this.accountID = UUID.randomUUID();
        this.userName = "";
        this.password = "";
        this.score = 0;
        this.rank = 0;
        this.achievements = new ArrayList<>();
    }

    /**
     * Creates an account with a username and password.
     * @param userName the username for the account
     * @param password the password for the account
     */
    public Account(String userName, String password) {
        this.accountID = UUID.randomUUID();
        this.userName = userName == null ? "" : userName.trim();
        this.password = password == null ? "" : password.trim();
        this.score = 0;
        this.rank = 0;
        this.achievements = new ArrayList<>();
    }

    /**
     * Gets the account ID as a string.
     * @return account ID
     */
    public String getAccountID() {
        return accountID == null ? "" : accountID.toString();
    }

    /**
     * Sets the account ID from a string.
     * If invalid, generates a new random ID.
     * @param id the ID string
     */
    public void setAccountID(String id) {
        try {
            this.accountID = UUID.fromString(id);
        } catch (Exception e) {
            this.accountID = UUID.randomUUID();
        }
    }

    /**
     * Checks if the given username and password match this account.
     * @param user the username to check
     * @param pass the password to check
     * @return true if login is successful, false otherwise
     */
    public boolean login(String user, String pass) {
        if (user == null || pass == null) return false;
        if (this.userName == null || this.password == null) return false;
        if (!this.userName.equalsIgnoreCase(user)) return false;
        return pass.equals(this.password);
    }

    /**
     * Updates the user's score by adding or subtracting points.
     * Ensures score stays within valid range.
     * @param points the points to add or subtract
     */
    public void updateScore(int points) {
        if (points == 0) return;
        long updated = (long) this.score + points;
        this.score = (int) Math.max(0, Math.min(Integer.MAX_VALUE, updated));
    }

    /**
     * Adds an achievement to the user's list if not already present.
     * @param a the achievement to add
     */
    public void addAchievements(Achievement a) {
        if (a == null) return;
        if (achievements == null) achievements = new ArrayList<>();
        if (!achievements.contains(a)) achievements.add(a);
    }

    /**
     * Gets a copy of the user's achievements list.
     * @return list of achievements
     */
    public List<Achievement> getAchievements() {
        return new ArrayList<>(achievements);
    }

    /**
     * Gets the username.
     * @return username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the username.
     * @param userName the username to set
     */
    public void setUserName(String userName) {
        this.userName = userName == null ? "" : userName.trim();
    }

    /**
     * Gets the password.
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password == null ? "" : password.trim();
    }

    /**
     * Gets the current score.
     * @return score
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score.
     * @param score the score to set
     */
    public void setScore(int score) {
        this.score = Math.max(0, score);
    }

    /**
     * Gets the user's rank.
     * @return rank
     */
    public int getRank() {
        return rank;
    }

    /**
     * Sets the user's rank.
     * @param rank the rank to set
     */
    public void setRank(int rank) {
        this.rank = Math.max(0, rank);
    }
}
