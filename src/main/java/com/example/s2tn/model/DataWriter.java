package com.example.s2tn.model;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DataWriter {

    private static final Path USERS_PATH = Paths.get("../../../json/users.json");
    private static final Path ROOMS_PATH = Paths.get("../../../json/rooms.json");

    public void saveUsers() {
        writeJson(USERS_PATH, new Object());
    }

    public void saveDungeon() {
        writeJson(ROOMS_PATH, new Object());
    } 

    public void saveGame() {
    }

    // public void saveLeaderboard(Leaderboard lb) {
    // }

    private void writeJson(Path p, Object obj) {
        if (p != null && obj != null) {
            System.out.println("JSON file path: " + p.toAbsolutePath());
        }
    }
}
