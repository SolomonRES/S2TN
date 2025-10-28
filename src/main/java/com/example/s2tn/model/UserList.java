package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Manages all user accounts in memory using a singleton pattern.
 * Provides lookup, add, remove, and update operations for users.
 */
public class UserList {

    private static final UserList INSTANCE = new UserList();

    private final List<Account> users = new ArrayList<>();

    /** Private constructor to enforce singleton usage. */
    private UserList() {}

    /** Returns the single shared instance of the UserList. */
    public static UserList getInstance() {
        return INSTANCE;
    }

    /** Returns an unmodifiable snapshot of all users. */
    public List<Account> getAll() {
        return List.copyOf(users);
    }

    /** Replaces the entire user list with a fresh collection (used by DataLoader). */
    public void replaceAll(List<Account> fresh) {
        users.clear();
        if (fresh != null) users.addAll(fresh);
    }

    /** Finds a user by account ID. Returns null if not found or ID is invalid. */
    public Account getId(String accountId) {
        if (accountId == null || accountId.isBlank()) return null;
        for (Account a : users) {
            if (accountId.equals(a.getAccountID())) return a;
        }
        return null;
    }

    /** Finds a user by username. Returns null if not found or input is invalid. */
    public Account getUser(String userName) {
        if (userName == null || userName.isBlank()) return null;
        for (Account user : users) {
            if (userName.equals(user.getUserName())) return user;
        }
        return null;
    }

    /** Finds a user by username (alias of getUser). Returns null if not found. */
    public Account getUserName(String userName) {
        if (userName == null || userName.isBlank()) return null;
        for (Account a : users) {
            if (userName.equals(a.getUserName())) return a;
        }
        return null;
    }

    /** Adds a new user if their ID and username are unique. Returns true if added. */
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

    /** Removes a user by account ID. Returns true if a matching user was removed. */
    public boolean removeUser(String accountId) {
        if (accountId == null || accountId.isBlank()) return false;
        return users.removeIf(u -> accountId.equals(u.getAccountID()));
    }

    /**
     * Updates an existing user with new data.
     * Returns true if successful, false if user not found or username conflicts.
     */
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
