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

    public boolean login(String user, String pass) {
        return false;
    }

    public void updateScore(int points) {
    }

    public void addAchievements(Achievement a) {
    }

    public List<Achievement> getAchievements() {
        return new ArrayList<>();
    }

    public int getRank() {
        return rank;
    }
}
