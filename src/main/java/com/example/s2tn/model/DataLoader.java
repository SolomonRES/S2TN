package com.example.s2tn.model;

import java.util.ArrayList;

public class DataLoader {
    
    public ArrayList<Dungeon> getDungeons() {
        return new ArrayList<>();
    }

    // public List<Account> getUsers() {
    //     return new ArrayList<>();
    // }

    // public ArrayList<Save> getSaves() {
    //     return new ArrayList<>();
    // }

    public Leaderboard getLeaderboard() {
        return new Leaderboard();
    }
}
