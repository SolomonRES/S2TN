package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Account {

    private UUID accountID;
    private String userName;
    private String passwordHash;
    private int score;
    private int rank;
    private List<Achievement> achievements;

    public Account() {
        this.accountID = UUID.randomUUID();
        this.userName = "";
        this.passwordHash = "";
        this.score = 0;
        this.rank = 0;
        this.achievements = new ArrayList<>();
    }
    
    public String getAccountID() { return accountID == null ? "" : accountID.toString(); }
    public void setAccountID(String id) {
        try { this.accountID = UUID.fromString(id); }
        catch (Exception e) { this.accountID = UUID.randomUUID(); }
    }

    public boolean login(String user, String pass) {
    if (user == null || pass == null) return false;
    if (this.userName == null || this.passwordHash == null) return false;
    if (!this.userName.equalsIgnoreCase(user)) return false;
    return pass.equals(this.passwordHash);
}

    public void updateScore(int points) {
    }

    public void addAchievements(Achievement a) {
    }

    public List<Achievement> getAchievements() {
        return new ArrayList<>();
    }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
