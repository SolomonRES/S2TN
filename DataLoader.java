package com.example.s2tn.model;

import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DataLoader extends DataConstants {
    
    public void loadUsers() {
        var path = usersPath();
        var loaded = new ArrayList<Account>();

        try {
            if (!Files.exists(path)) {
                UserList.getInstance().replaceAll(loaded);
                return;
            }

            var parser = new org.json.simple.parser.JSONParser();
            try (var reader = new FileReader(path.toFile())) {
                var root = parser.parse(reader);
                var arr  = (org.json.simple.JSONArray) root;

                for (Object o : arr) {
                    var u = (org.json.simple.JSONObject) o;

                    var a = new Account();
                    a.setAccountID(asString(u.get("accountID")));
                    a.setUserName(asString(u.get("userName")));
                    a.setPasswordHash(asString(u.get("password")));
                    a.setScore(asInt(u.get("score")));
                    a.setRank(asInt(u.get("rank")));
                    // do the achievements later

                    loaded.add(a);
                }
            }
        } catch (Exception e) {
            System.err.println("not loading the user path " + path.toAbsolutePath() + ": " + e.getMessage());
        }

        UserList.getInstance().replaceAll(loaded);
    }

    private static String asString(Object v) { return v == null ? null : String.valueOf(v); }
    private static int asInt(Object v) {
        if (v instanceof Number n) return n.intValue();
        if (v == null) return 0;
        try { return Integer.parseInt(String.valueOf(v)); } catch (NumberFormatException e) { return 0; }
    }
    
    public List<Dungeon> getDungeons() {
        return new ArrayList<>();
    }

    public List<Progress> getSaves() {
        return new ArrayList<>();
    }
    
    public Leaderboard getLeaderboard() {
        return new Leaderboard();
    }
}
