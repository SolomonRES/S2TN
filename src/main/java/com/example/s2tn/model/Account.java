package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Account {

    private UUID accountID;
    private String userName;
    private String password;
    private int score;
    private int rank;
    private List<Achievement> achievements;

    public Account() {
        this.accountID = UUID.randomUUID();
        this.userName = "";
        this.password = "";
        this.score = 0;
        this.rank = 0;
        this.achievements = new ArrayList<>();
    }

    public Account(String userName, String password) {
        this.accountID = UUID.randomUUID();
        this.userName = userName == null ? "" : userName.trim();
        this.password = password == null ? "" : password.trim();
        this.score = 0;
        this.rank = 0;
        this.achievements = new ArrayList<>();
    }

    public String getAccountID() {
        return accountID == null ? "" : accountID.toString();
    }

    public void setAccountID(String id) {
        try {
            this.accountID = UUID.fromString(id);
        } catch (Exception e) {
            this.accountID = UUID.randomUUID();
        }
    }

    public boolean login(String user, String pass) {
        if (user == null || pass == null) return false;
        if (this.userName == null || this.password == null) return false;
        if (!this.userName.equalsIgnoreCase(user)) return false;
        return pass.equals(this.password);
    }

    public void updateScore(int points) {
        if (points == 0) return;
        long updated = (long) this.score + points;
        this.score = (int) Math.max(0, Math.min(Integer.MAX_VALUE, updated));
    }

    public void addAchievements(Achievement a) {
        if (a == null) return;
        if (achievements == null) achievements = new ArrayList<>();
        if (!achievements.contains(a)) achievements.add(a);
    }

    public List<Achievement> getAchievements() {
        return new ArrayList<>(achievements);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? "" : userName.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? "" : password.trim();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = Math.max(0, score);
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = Math.max(0, rank);
    }
}
