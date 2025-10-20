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

    @SuppressWarnings("UseSpecificCatch")
    public void loadUsers() {
        Path path = usersPath();
        ArrayList<Account> loaded = new ArrayList<>();

        try {
            if (!Files.exists(path)) {
                UserList.getInstance().replaceAll(loaded);
                return;
            }

            JSONParser parser = new JSONParser();
            try (FileReader reader = new FileReader(path.toFile())) {
                Object root = parser.parse(reader);
                if (!(root instanceof JSONArray arr)) {
                    System.err.println("Users JSON must be an array: " + path.toAbsolutePath());
                    UserList.getInstance().replaceAll(loaded);
                    return;
                }

                for (Object o : arr) {
                    if (!(o instanceof JSONObject u)) continue;

                    Account a = new Account();
                    a.setAccountID(asString(u.get("accountID")));   // Account has setAccountID(String)
                    a.setUserName(asString(u.get("userName")));

                    
                    String pass = asString(u.get("password"));
                    if (pass == null || pass.isEmpty()) pass = asString(u.get("passwordHash"));
                    a.setPasswordHash(pass); 
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

    // dungeons 

    @SuppressWarnings("UseSpecificCatch")
    public void loadDungeons() {
        dungeons.clear();

        try {
            Path path = dungeonPath();
            if (!Files.exists(path)) {
                System.err.println("Dungeon file not found at: " + path.toAbsolutePath());
                return;
            }

            JSONParser parser = new JSONParser();
            try (FileReader reader = new FileReader(path.toFile())) {
                Object rootObj = parser.parse(reader);

                JSONArray dungeonArray;
                if (rootObj instanceof JSONObject root && root.get("dungeons") instanceof JSONArray arr) {
                    dungeonArray = arr; 
                } else if (rootObj instanceof JSONArray arr) {
                    dungeonArray = arr; 
                } else {
                    System.err.println("Unexpected dungeon JSON format at: " + path.toAbsolutePath());
                    return;
                }

                for (Object dObj : dungeonArray) {
                    if (!(dObj instanceof JSONObject dungeonJson)) continue;

                    String name = asString(dungeonJson.get("name"));
                    if (name == null) name = "Unnamed Dungeon";

                    Difficulty difficulty = parseDifficulty(dungeonJson.get("difficulty"));

                    double baseMaxAllowedTime = 600_000d; // 10 minutes 
                    Object timerObj = dungeonJson.get("timer");
                    if (timerObj instanceof JSONObject tJson) {
                        String allowed = asString(tJson.get("allowedTime"));
                        if (allowed != null) {
                            baseMaxAllowedTime = toMillis(allowed, 600_000d);
                        }
                    }

                    // --- Rooms
                    ArrayList<Room> rooms = new ArrayList<>();
                    Object roomsObj = dungeonJson.get("rooms");
                    if (roomsObj instanceof JSONArray roomsArray) {
                        for (Object rObj : roomsArray) {
                            if (!(rObj instanceof JSONObject roomJson)) continue;

                            // Room-level hints
                            ArrayList<Hint> roomHints = new ArrayList<>();
                            Object hintsObj = roomJson.get("hints");
                            if (hintsObj instanceof JSONArray hArr) {
                                for (Object hObj : hArr) {
                                    if (hObj instanceof JSONObject hJson) {
                                        int level = asInt(hJson.get("level"));
                                        String text = asString(hJson.get("text"), "");
                                        double cost = asDouble(hJson.get("cost"), 0.0);
                                        roomHints.add(new Hint(level, text, cost));
                                    }
                                }
                            }

                            // puzzles in the room
                            ArrayList<Puzzle> puzzles = new ArrayList<>();
                            Object puzzlesObj = roomJson.get("puzzles");
                            if (puzzlesObj instanceof JSONArray pArr) {
                                for (Object pObj : pArr) {
                                    if (pObj instanceof JSONObject pJson) {
                                        Puzzle pz = parsePuzzle(pJson);
                                        if (pz != null) puzzles.add(pz);
                                    }
                                }
                            }

                            Room room = new Room(puzzles, roomHints, new ArrayList<>(), new ArrayList<>());
                            rooms.add(room);
                        }
                    }

                    Room starting = rooms.isEmpty() ? null : rooms.get(0);
                    Dungeon dungeon = new Dungeon(name, rooms, baseMaxAllowedTime, difficulty, starting);
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

    private static String asString(Object v) { return v == null ? null : String.valueOf(v); }
    private static String asString(Object v, String def) { return v == null ? def : String.valueOf(v); }

    private static int asInt(Object v) {
        if (v instanceof Number n) return n.intValue();
        if (v == null) return 0;
        try { return Integer.parseInt(String.valueOf(v)); } catch (NumberFormatException e) { return 0; }
    }

    private static double asDouble(Object v, double def) {
        if (v == null) return def;
        if (v instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(String.valueOf(v)); } catch (NumberFormatException e) { return def; }
    }

    private static double toMillis(String hms, double def) {
        if (hms == null || !hms.matches("\\d{2}:\\d{2}:\\d{2}")) return def;
        String[] p = hms.split(":");
        long h = Long.parseLong(p[0]), m = Long.parseLong(p[1]), s = Long.parseLong(p[2]);
        return (h * 3600 + m * 60 + s) * 1000.0;
    }

    private static Difficulty parseDifficulty(Object raw) {
        if (raw == null) return Difficulty.NORMAL;
        if (raw instanceof Number n) {
            int v = n.intValue();
            return switch (v) {
                case 1 -> Difficulty.EASY;
                case 3, 4 -> Difficulty.HARD;
                default -> Difficulty.NORMAL;
            };
        }
        String s = String.valueOf(raw).toLowerCase();
        return switch (s) {
            case "easy" -> Difficulty.EASY;
            case "hard" -> Difficulty.HARD;
            default -> Difficulty.NORMAL;
        };
    }

    private static Puzzle parsePuzzle(JSONObject pJson) {
        String title = asString(pJson.get("title"), "Puzzle");
        Puzzle pz;

        if (pJson.get("question") != null && pJson.get("answer") != null) {
            String q = asString(pJson.get("question"), "");
            String a = asString(pJson.get("answer"), "");
            String singleHint = asString(pJson.get("hint"), null);
            pz = new Riddle(title, q, a, singleHint);
        } else {
            
            CodePuzzle cp = new CodePuzzle(title, null);
            Object codes = pJson.get("acceptedCodes");
            if (codes instanceof JSONArray arr) {
                for (Object o : arr) if (o instanceof String s) cp.addAcceptedCode(s);
            }
            if (pJson.get("solution") != null) {
                cp.addAcceptedCode(asString(pJson.get("solution"), ""));
            }
            pz = cp;
        }

        Object hintsObj = pJson.get("hints");
        if (hintsObj instanceof JSONArray hArr) {
            int count = 0;
            for (Object hObj : hArr) {
                if (hObj instanceof JSONObject hJson) {
                    int level = asInt(hJson.get("level"));
                    String text = asString(hJson.get("text"), "");
                    double cost = asDouble(hJson.get("cost"), 0.0);
                    try { pz.addHint(new Hint(level, text, cost)); count++; } catch (Throwable ignore) {}
                }
            }
            try { pz.setMaxHints(count); } catch (Throwable ignore) {}
        }

        return pz;
    }
}
