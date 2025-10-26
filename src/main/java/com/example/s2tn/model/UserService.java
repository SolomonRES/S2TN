package com.example.s2tn.model;

import java.util.List;
import java.util.UUID;

/**
 * Provides operations for managing user accounts, including add, update, remove, and retrieval.
 * Delegates persistence to {@link DataWriter} and data access to {@link UserList}.
 */
public class UserService {

    private final UserList users = UserList.getInstance();
    private final DataWriter writer = new DataWriter();

    /** 
     * Adds a new user after validating for duplicates and missing data.
     * Saves changes if successful.
     * 
     * @param a the account to add
     * @return true if added successfully, false otherwise
     */
    public boolean addUser(Account a) {
        if (a == null) return false;

        if (a.getUserName() == null || a.getUserName().isBlank()) return false;
        if (getByUserName(a.getUserName()) != null) return false;

        if (a.getAccountID() == null || a.getAccountID().isBlank()) {
            a.setAccountID(UUID.randomUUID().toString());
        }

        boolean ok = UserList.getInstance().addUser(a);
        if (ok) new DataWriter().saveUsers();
        return ok;
    }

    /**
     * Updates an existing user record.
     * Saves changes if successful.
     * 
     * @param updated the updated account object
     * @return true if updated successfully, false otherwise
     */
    public boolean updateUser(Account updated) {
        boolean ok = users.updateUser(updated);
        if (ok) writer.saveUsers();
        return ok;
    }

    /**
     * Removes a user by account ID.
     * Saves changes if successful.
     * 
     * @param accountId the ID of the account to remove
     * @return true if removed successfully, false otherwise
     */
    public boolean removeUser(String accountId) {
        boolean ok = users.removeUser(accountId);
        if (ok) writer.saveUsers();
        return ok;
    }

    /** Returns a list of all users. */
    public List<Account> listUsers() { return users.getAll(); }

    /** Returns a user by their account ID, or null if not found. */
    public Account getById(String id) { return users.getId(id); }

    /** Returns a user by username, or null if not found. */
    public Account getByUserName(String name) { return users.getUserName(name); }
}
