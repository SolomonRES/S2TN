package com.example.s2tn.model;

import java.util.ArrayList; 
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserList {
    
    private static UserList instance;
    private final List<Account> users;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private UserList() {
        users = new ArrayList<>();
    }

    public static synchronized UserList getInstance() {
        if (instance == null) instance = new UserList();
        return instance;
    }

    public List<Account> getAll() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(users));
        } finally {
            lock.readLock().unlock();
        }
    }

    public Account getId(String accountId) {
        if (accountId == null || accountId.isBlank()) return null;
        lock.readLock().lock();
        try {
            for (Account user : users) {
                if (accountId.equals(user.getAccountID())) {
                    return user;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Account getUser(String userName) {
        if (userName == null || userName.isBlank()) return null;
        lock.readLock().lock();
        try {
            for (Account user : users) {
                if (userName.equals(user.getUserName())) {
                    return user;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean addUser(Account a) {
        if (a == null) return false;

        if (a.getAccountID() == null || a.getAccountID().isBlank()) {
            a.setAccountID((UUID.randomUUID().toString()));
        }

        lock.writeLock().lock();
        try {
            boolean idExists = users.stream()
                    .anyMatch(u -> a.getAccountID().equals(u.getAccountID()));
            boolean nameTaken = users.stream()
                    .anyMatch(u -> a.getUserName() !=null &&
                                   a.getUserName().equals(u.getUserName()));
            if (idExists || nameTaken) return false;

            users.add(a);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
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
                
                lock.writeLock().lock();
                try {
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
                } finally {
                    lock.writeLock().unlock();
                }
            }
        } catch (Exception e) {
            System.err.println("load users failed at " + path.toAbsolutePath() + ": " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public void saveToFile() {
        var path = DataConstants.usersPath();
        var arr  = new org.json.simple.JSONArray();
        
        lock.readLock().lock();
        try {
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
        } finally {
            lock.readLock().unlock();
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

    private static String asString(Object v) { 
        return v == null ? "" : String.valueOf(v); 
    }
    
    private static int asInt(Object v) {
        if (v instanceof Number n) return n.intValue();
        if (v == null) return 0;
        try { 
            return Integer.parseInt(String.valueOf(v)); 
        } catch (NumberFormatException e) { 
            return 0; 
        }
    }

    public boolean removeUser(String accountId) {
        if (accountId == null || accountId.isBlank()) return false;
        lock.writeLock().lock();
        try {
            return users.removeIf(u -> accountId.equals(u.getAccountID()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean updateUser(Account updated) {
    if (updated == null || updated.getAccountID() == null || updated.getAccountID().isBlank()) return false;
    lock.writeLock().lock();
    try {
        for (int i = 0; i < users.size(); i++) {
            Account cur = users.get(i);
            if (updated.getAccountID().equals(cur.getAccountID())) {
                
                boolean nameTaken = users.stream()
                    .anyMatch(u -> !u.getAccountID().equals(updated.getAccountID())
                                && updated.getUserName() != null
                                && updated.getUserName().equals(u.getUserName()));
                if (nameTaken) return false;
                users.set(i, updated);
                return true;
            }
        }
        return false;
    } finally {
        lock.writeLock().unlock();
    }
}

} 
