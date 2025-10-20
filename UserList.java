package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class UserList {

    private static final UserList INSTANCE = new UserList();

    private final List<Account> users = new ArrayList<>();

    private UserList() {}

    public static UserList getInstance() {
        return INSTANCE;
    }

    // snapshot of users (not changeable)
    public List<Account> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(users));
    }

    // used by the DataLoader 
    public void replaceAll(List<Account> fresh) {
        users.clear();
        if (fresh != null) users.addAll(fresh);
    }

    public Account getId(String accountId) {
        if (accountId == null || accountId.isBlank()) return null;
        for (Account a : users) {
            if (accountId.equals(a.getAccountID())) return a;
        }
        return null;
    }

    public Account getUser(String userName) {
        if (userName == null || userName.isBlank()) return null;
        for (Account user : users) {
            if (userName.equals(user.getUserName())) return user;
        }
        return null;
    }

    public Account getUserName(String userName) {
        if (userName == null || userName.isBlank()) return null;
        for (Account a : users) {
            if (userName.equals(a.getUserName())) return a;
        }
        return null;
    }

    public boolean addUser(Account a) {
        if (a == null) return false;

        if (a.getAccountID() == null || a.getAccountID().isBlank()) {
            a.setAccountID(UUID.randomUUID().toString());
        }

        boolean idExists = users.stream()
                .anyMatch(u -> a.getAccountID().equals(u.getAccountID()));
        boolean nameTaken = users.stream()
                .anyMatch(u -> a.getUserName() != null &&
                               a.getUserName().equals(u.getUserName()));
        if (idExists || nameTaken) return false;

        users.add(a);
        return true;
    }

    public boolean removeUser(String accountId) {
        if (accountId == null || accountId.isBlank()) return false;
        return users.removeIf(u -> accountId.equals(u.getAccountID()));
    }

    public boolean updateUser(Account updated) {
        if (updated == null || updated.getAccountID() == null || updated.getAccountID().isBlank()) return false;

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
    }

} 
