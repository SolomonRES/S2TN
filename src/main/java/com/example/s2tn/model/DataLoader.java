package com.example.s2tn.model;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DataLoader extends DataConstants {

    private final ArrayList<Dungeon> dungeons = new ArrayList<>();

    public void loadUsers() {
        var path = usersPath();
        var loaded = new ArrayList<Account>();

        try {
            if (!Files.exists(path)) {
                UserList.getInstance().replaceAll(loaded);
                return;
            }

            var parser = new JSONParser();
            try (var reader = new FileReader(path.toFile())) {
                var root = parser.parse(reader);
                var arr = (JSONArray) root;

                for (Object o : arr) {
                    var u = (JSONObject) o;
                    var a = new Account();
                    a.setAccountID(asString(u.get("accountID")));
                    a.setUserName(asString(u.get("userName")));
                    a.setPasswordHash(asString(u.get("password")));
                    a.setScore(asInt(u.get("score")));
                    a.setRank(asInt(u.get("rank")));
                    loaded.add(a);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load users from " + path.toAbsolutePath() + ": " + e.getMessage());
        }

        UserList.getInstance().replaceAll(loaded);
    }

    private static String asString(Object v) { return v == null ? null : String.valueOf(v); }
    private static int asInt(Object v) {
        if (v instanceof Number n) return n.intValue();
        if (v == null) return 0;
        try { return Integer.parseInt(String.valueOf(v)); } catch (NumberFormatException e) { return 0; }
    }

    public void loadDungeons() {
        dungeons.clear();
        try {
            java.nio.file.Path path = dungeonPath();
            if (!java.nio.file.Files.exists(path)) {
                System.err.println("Dungeon file not found at: " + path.toAbsolutePath());
                return;
            }

            org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
            try (java.io.FileReader reader = new java.io.FileReader(path.toFile())) {
                Object root = parser.parse(reader);

                if (!(root instanceof org.json.simple.JSONArray arr)) {
                    System.err.println("Unexpected JSON format for dungeons file (expected array).");
                    return;
                }

                for (Object dObj : arr) {
                    org.json.simple.JSONObject dJson = (org.json.simple.JSONObject) dObj;

                    String name = asString(dJson.get("name"));
                    if (name == null || name.isBlank()) name = "Dungeon";

                    int diffNum = asInt(dJson.get("difficulty"));
                    Difficulty difficulty = (diffNum <= 1) ? Difficulty.EASY
                                            : (diffNum == 2) ? Difficulty.NORMAL
                                                            : Difficulty.HARD;

                    // rooms array (rooms are empty for now)
                    java.util.ArrayList<Room> rooms = new java.util.ArrayList<>();
                    Object roomsObj = dJson.get("rooms");
                    if (roomsObj instanceof org.json.simple.JSONArray roomsArr) {
                        for (Object rObj : roomsArr) {
                            rooms.add(new Room("Room"));
                        }
                    }

                    Room starting = rooms.isEmpty() ? null : rooms.get(0);

                    double baseMaxAllowedTime = 600_000d;

                    Dungeon dungeon = new Dungeon(name,
                                                rooms,
                                                baseMaxAllowedTime,
                                                difficulty,
                                                starting);

                    dungeons.add(dungeon);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading dungeons: " + e.getMessage());
        }
    }


    public List<Dungeon> getDungeons() {
        return new ArrayList<>(dungeons);
    }

    public List<Progress> getSaves() {
        return new ArrayList<>();
    }

    public Leaderboard getLeaderboard() {
        return new Leaderboard();
    }
}
