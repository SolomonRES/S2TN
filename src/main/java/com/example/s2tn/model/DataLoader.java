package com.example.s2tn.model;

import java.util.ArrayList;

public class DataLoader {
    
    public ArrayList<Dungeon> getDungeons() {
        return new ArrayList<>();
    }

    public java.util.List<Account> getUsers() {
        UserList.getInstance().loadFromFile();
        return new ArrayList<>(UserList.getInstance().getAll());
    }

    public ArrayList<Progress> getSaves() {
        return new ArrayList<>();
    }

    public Leaderboard getLeaderboard() {
        return new Leaderboard();
    }
}
