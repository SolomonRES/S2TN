package com.example.s2tn.model;

import java.util.List;
import java.util.UUID;

public class UserService {

    private final UserList users = UserList.getInstance();
    private final DataWriter writer = new DataWriter();

    // add user validation 
    public boolean addUser(Account a) {
        if (a == null) return false;
        if (a.getAccountID() == null || a.getAccountID().isBlank()) {
            a.setAccountID(UUID.randomUUID().toString());
        }
        boolean ok = users.addUser(a);
        if (ok) writer.saveUsers();
        return ok;
    }

    // update user validation 
    public boolean updateUser(Account updated) {
        boolean ok = users.updateUser(updated);
        if (ok) writer.saveUsers();
        return ok;
    }

    // remove user validation 
    public boolean removeUser(String accountId) {
        boolean ok = users.removeUser(accountId);
        if (ok) writer.saveUsers();
        return ok;
    }

    // helpers for reading
    public List<Account> listUsers() { return users.getAll(); }
    public Account getById(String id) { return users.getId(id); }
    public Account getByUserName(String name) { return users.getUserName(name); }
}
