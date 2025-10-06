package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard {

    private List<Leaderboard> entries;
    private String userName;
    private int score;
    private long elapsedTime;
    private long timestamp;

    public Leaderboard() {
        this.entries = new ArrayList<>();
    }

    public void submit(String userName, int score, long elapsedTime) {
    }

    public List<Leaderboard> topScore(int n) {
        return new ArrayList<>();
    }
}
