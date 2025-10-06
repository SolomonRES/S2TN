package com.example.s2tn.model;


import java.util.*;

public class UserList {
    private static UserList instance;
    private final List<Account> users;

    private UserList() {
        users = new ArrayList<>();
    }

    public static synchronized UserList getInstance() {
        if (instance == null) instance = new UserList();
        return instance;
    }

    public List<Account> getAll() {
        return Collections.unmodifiableList(users);
    }

    public Account getUser(String id) {
        return null;
    }

    public void addUser(Account a) {
        if (a != null) users.add(a);
    }

    public void loadFromFile() {
        var path = DataConstants.usersPath();
        try {
            if (!java.nio.file.Files.exists(path)) {
                System.err.println("users.json not found at: " + path.toAbsolutePath());
                return; // leave list empty
            }
            var parser = new org.json.simple.parser.JSONParser();
            try (var reader = new java.io.FileReader(path.toFile())) {
                var root = parser.parse(reader);
                var arr = (org.json.simple.JSONArray) root;
                users.clear();
                for (Object o : arr) {
                    var u = (org.json.simple.JSONObject) o;
                    String accountId = asString(u.get("accountID"));
                    String userName  = asString(u.get("userName"));
                    String password  = asString(u.get("password"));
                    int score        = asInt(u.get("score"));
                    int rank         = asInt(u.get("rank"));

                    var a = new Account();
                    a.setAccountID(accountId);
                    a.setUserName(userName);
                    a.setPasswordHash(password);
                    a.setScore(score);
                    a.setRank(rank);
                    users.add(a);
                }
            }
        } catch (Exception e) {
            System.err.println("load users failed at " + path.toAbsolutePath() + ": " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public void saveToFile(String path2) {
        var path = DataConstants.usersPath();
        var arr  = new org.json.simple.JSONArray();
        for (Account a : users) {
            var o = new org.json.simple.JSONObject();
            o.put("accountID", a.getAccountID());
            o.put("userName",  a.getUserName());
            o.put("password",  a.getPasswordHash());
            o.put("score",     a.getScore());
            o.put("rank",      a.getRank());
            o.put("achievements", new org.json.simple.JSONArray());
            arr.add(o);
        }
        try {
            var parent = path.getParent();
            if (parent != null) java.nio.file.Files.createDirectories(parent); // <-- fixes FileNotFoundException
            try (var w = new java.io.FileWriter(path.toFile())) {
                w.write(arr.toJSONString());
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException("saveToFile users failed at " + path.toAbsolutePath(), e);
        }
    }

    private static String asString(Object v) { return v == null ? "" : String.valueOf(v); }
    private static int asInt(Object v) {
        if (v == null) return 0;
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(v)); } catch (Exception e) { return 0; }
    }

    // public void removeUser(String id) {
    // }

    // public String toJSON() {
    //     return "";
    // }

    // public void fromJSON(String json) {
    // }
} 
