package com.example.s2tn.model;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

public class DataWriter extends DataConstants {

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

    public void saveGame() {
        // placeholder for game save logic
    }

    public void saveLeaderboard(Leaderboard lb) {
        Path path = lbPath();
        writeJson(path, lb);
    }


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

    private static String userToJson(Account a) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("{");
        sb.append("\"accountID\":").append(toJsonString(a.getAccountID()));
        sb.append(",\"userName\":").append(toJsonString(a.getUserName()));
        sb.append(",\"password\":").append(toJsonString(a.getPasswordHash()));
        sb.append(",\"score\":").append(a.getScore());
        sb.append(",\"rank\":").append(a.getRank());
        sb.append(",\"achievements\":[]");
        sb.append("}");
        return sb.toString();
    }

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

    private void writeJson(Path path, Object obj) {
        if (path != null && obj != null) {
            System.out.println("JSON file path: " + path.toAbsolutePath());
        }
    }

    private static String toJsonString(Object v) {
        if (v == null) return "null";
        return "\"" + jsonEscape(String.valueOf(v)) + "\"";
    }

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

    private static String unescapeJson(String s) {
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
