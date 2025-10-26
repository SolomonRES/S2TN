package com.example.s2tn.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

/**
 * Handles writing user, dungeon, leaderboard, and game data to JSON files.
 * Extends {@link DataConstants} for consistent file path references.
 */
public class DataWriter extends DataConstants {

    /**
     * Saves all users from {@link UserList} to the users.json file.
     * Appends only new users that are not already in the file.
     */
    @SuppressWarnings("UseSpecificCatch")
    public void saveUsers() {
        Path path = usersPath();
        List<Account> currentUsers = UserList.getInstance().getAll();

        try {
            String existing = "";
            if (Files.exists(path)) {
                existing = Files.readString(path, StandardCharsets.UTF_8).trim();
            }

            if (existing.isEmpty()) existing = "[]";
            if (!existing.startsWith("[")) existing = "[]";  

            HashSet<String> seenUsernames = new HashSet<>();
            collectExistingUsernames(existing, seenUsernames);

            StringBuilder appended = new StringBuilder();
            boolean firstAppend = true;
            for (Account a : currentUsers) {
                String uname = a.getUserName() == null ? "" : a.getUserName();
                if (uname.isEmpty() || seenUsernames.contains(uname)) continue;

                String obj = userToJson(a);
                if (!firstAppend) appended.append(",");
                appended.append(obj);
                firstAppend = false;
            }

            if (appended.length() == 0 && Files.exists(path)) {
                return;
            }

            StringBuilder out = new StringBuilder();
            String trimmed = existing;
            if (trimmed.endsWith("]")) trimmed = trimmed.substring(0, trimmed.length() - 1);

            boolean hadObjects = trimmed.indexOf('{') >= 0; 
            out.append(trimmed);
            if (hadObjects && appended.length() > 0) out.append(",");
            out.append(appended);
            out.append("]");

            Path parent = path.getParent();
            if (parent != null) Files.createDirectories(parent);
            try (FileWriter writer = new FileWriter(path.toFile(), false)) {
                writer.write(out.toString());
            }
        } catch (Exception e) {
            System.err.println("Failed to append users at " + path.toAbsolutePath() + ": " + e.getMessage());
        }
    }

    /**
     * Saves a list of dungeons to the rooms.json file.
     * Converts dungeons and their rooms into a JSON array format.
     *
     * @param dungeons list of dungeons to save
     */
    @SuppressWarnings("UseSpecificCatch")
    public void saveDungeons(List<Dungeon> dungeons) {
        Path path = dungeonPath();

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < dungeons.size(); i++) {
            Dungeon d = dungeons.get(i);

            String name = safeDungeonName(d);
            String diff = safeDifficulty(d);

            sb.append("{");
            sb.append("\"name\":").append(toJsonString(name));
            sb.append(",\"difficulty\":").append(toJsonString(diff));
            sb.append(",\"rooms\":[");

            List<Room> rooms = d.getRooms();
            for (int j = 0; j < rooms.size(); j++) {
                sb.append("{\"name\":").append(toJsonString("Room " + (j + 1))).append("}");
                if (j < rooms.size() - 1) sb.append(",");
            }

            sb.append("]}");
            if (i < dungeons.size() - 1) sb.append(",");
        }

        sb.append("]");

        try {
            Path parent = path.getParent();
            if (parent != null) Files.createDirectories(parent);
            try (FileWriter writer = new FileWriter(path.toFile(), false)) {
                writer.write(sb.toString());
            }
        } catch (Exception e) {
            System.err.println("Failed to save dungeons at " + path.toAbsolutePath() + ": " + e.getMessage());
        }
    }

    /**
     * Placeholder for future game save logic.
     */
    public void saveGame() {
        // placeholder for game save logic
    }

    /**
     * Saves leaderboard data to the leaderboard JSON file.
     *
     * @param lb the leaderboard to save
     */
    public void saveLeaderboard(Leaderboard lb) {
        Path path = lbPath();
        writeJson(path, lb);
    }

    /**
     * Collects existing usernames from a JSON array string and adds them to a set.
     *
     * @param jsonArray JSON string containing user data
     * @param out set to populate with usernames
     */
    private static void collectExistingUsernames(String jsonArray, HashSet<String> out) {
        int idx = 0;
        while (idx >= 0) {
            idx = jsonArray.indexOf("\"userName\"", idx);
            if (idx < 0) break;
            int colon = jsonArray.indexOf(':', idx);
            if (colon < 0) break;
            int firstQuote = jsonArray.indexOf('"', colon + 1);
            if (firstQuote < 0) break;
            int secondQuote = jsonArray.indexOf('"', firstQuote + 1);
            if (secondQuote < 0) break;
            String uname = jsonArray.substring(firstQuote + 1, secondQuote);
            out.add(unescapeJson(uname));
            idx = secondQuote + 1;
        }
    }

    /**
     * Converts an {@link Account} object to a JSON string.
     *
     * @param a the account to convert
     * @return JSON string representation of the account
     */
    private static String userToJson(Account a) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("{");
        sb.append("\"accountID\":").append(toJsonString(a.getAccountID()));
        sb.append(",\"userName\":").append(toJsonString(a.getUserName()));
        sb.append(",\"password\":").append(toJsonString(a.getPassword()));
        sb.append(",\"score\":").append(a.getScore());
        sb.append(",\"rank\":").append(a.getRank());
        sb.append(",\"achievements\":[]");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Safely retrieves a dungeon's name using reflection.
     *
     * @param d the dungeon to inspect
     * @return the dungeon name or "Dungeon" if unavailable
     */
    @SuppressWarnings("UseSpecificCatch")
    private static String safeDungeonName(Dungeon d) {
        try {
            var m = d.getClass().getMethod("getName");
            Object v = m.invoke(d);
            return v == null ? "Dungeon" : String.valueOf(v);
        } catch (Exception ignore) {
            return "Dungeon";
        }
    }

    /**
     * Safely retrieves a dungeon's difficulty using reflection.
     *
     * @param d the dungeon to inspect
     * @return the difficulty as a lowercase string or "normal"
     */
    @SuppressWarnings("UseSpecificCatch")
    private static String safeDifficulty(Dungeon d) {
        try {
            var m = d.getClass().getMethod("getDifficulty");
            Object v = m.invoke(d);
            return v == null ? "normal" : String.valueOf(v).toLowerCase();
        } catch (Exception ignore) {
            return "normal";
        }
    }

    /**
     * Writes JSON data to the given file path.
     *
     * @param path the target file path
     * @param obj the object to write
     */
    private void writeJson(Path path, Object obj) {
        if (path != null && obj != null) {
            System.out.println("JSON file path: " + path.toAbsolutePath());
        }
    }

    /**
     * Converts an object to a JSON-safe string.
     *
     * @param v object to convert
     * @return JSON string value or "null"
     */
    private static String toJsonString(Object v) {
        if (v == null) return "null";
        return "\"" + jsonEscape(String.valueOf(v)) + "\"";
    }

    /**
     * Escapes special characters in a string for JSON formatting.
     *
     * @param s input string
     * @return escaped JSON-safe string
     */
    private static String jsonEscape(String s) {
        StringBuilder out = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\b' -> out.append("\\b");
                case '\f' -> out.append("\\f");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> {
                    if (c < 0x20) out.append(String.format("\\u%04x", (int) c));
                    else out.append(c);
                }
            }
        }
        return out.toString();
    }

    /**
     * Reverses basic JSON string escaping.
     *
     * @param s JSON string
     * @return unescaped version
     */
    private static String unescapeJson(String s) {
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    /**
     * Writes a text certificate file for a completed game session.
     *
     * @param account player account
     * @param gameName name of the game
     * @param hintsUsed number of hints used
     * @param difficulty difficulty level
     * @param score final score
     */
    public static void writeCertificate(Account account, String gameName, int hintsUsed, String difficulty, int score) {
        try (PrintWriter writer = new PrintWriter("Certificate_" + account.getUserName() + ".txt")) {
            writer.println("===== Escape Room Certificate =====");
            writer.println("Player: " + account.getUserName());
            writer.println("Game: " + gameName);
            writer.println("Difficulty: " + difficulty);
            writer.println("Hints Used: " + hintsUsed);
            writer.println("Final Score: " + score);
            writer.println("Congratulations on escaping!");
        } catch (IOException e) {
        }
    }
}
