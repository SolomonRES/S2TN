package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.List;

public class UserList {
    private static UserList instance;
    private List<Account> users;

    private UserList() {
        users = new ArrayList<>();
    }

    public static UserList getInstance() {
        if (instance == null) {
            instance = new UserList();
        }
        return instance;
    }

    public Account getUser(String id) {
        return null;
    }

    public void addUser(Account a) {
    }

    public void removeUser(String id) {
    }

    public String toJSON() {
        return "";
    }

    public void fromJSON(String json) {
    }
}
