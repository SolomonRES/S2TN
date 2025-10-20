package com.example.s2tn.model;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataWriter extends DataConstants {
    
    // don't think I'll need these but keep them jsut in case 
    // private static final Path USERS_PATH = Paths.get("../../../json/users.json");
    private static final Path ROOMS_PATH = Paths.get("../../../json/rooms.json");

    public void saveUsers() {
        var path  = usersPath();                  // from DataConstants
        var users = UserList.getInstance().getAll();
        var arr   = new org.json.simple.JSONArray();

        for (Account a : users) {
            var o = new org.json.simple.JSONObject();
            o.put("accountID", a.getAccountID());
            o.put("userName",  a.getUserName());
            o.put("password",  a.getPasswordHash());
            o.put("score",     a.getScore());
            o.put("rank",      a.getRank());
            o.put("achievements", new org.json.simple.JSONArray()); // TODO
            arr.add(o);
        }

        try {
            Path parent = path.getParent();
            if (parent != null) Files.createDirectories(parent);
            try (var writer = new FileWriter(path.toFile(), false)) {
                writer.write(arr.toJSONString());
            }
        } catch (Exception e) {
            System.err.println("Failed to save users at " + path.toAbsolutePath() + ": " + e.getMessage());
        }
    }

    public void saveDungeon() {
        writeJson(ROOMS_PATH, new Object());
    } 

    public void saveGame() {
    }

    public void saveLeaderboard(Leaderboard lb) {
    }

    private void writeJson(Path p, Object obj) {
        if (p != null && obj != null) {
            System.out.println("JSON file path: " + p.toAbsolutePath());
        }
    }
}
