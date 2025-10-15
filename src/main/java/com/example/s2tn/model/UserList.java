package com.example.s2tn.model;

import java.util.ArrayList; 
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserList {
    
    private static volatile UserList instance;
    private final List<Account> users = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private UserList() {}

    public static UserList getInstance() {
        UserList ref = instance;
        if (ref == null) {
            synchronized (UserList.class) {
                ref = instance;
                if (ref == null) instance = ref = new UserList();
            }
        }
        return ref;
    }
    
    // snapshot of users (not changeable)
    public List<Account> getAll() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(users));
        } finally {
            lock.readLock().unlock();
        }
    }
    
    // used by the DataLoader 
    public void replaceAll(List<Account> fresh) {
        lock.writeLock().lock();
        try {
            users.clear();
            if (fresh != null) users.addAll(fresh);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Account getId(String accountId) {
        if (accountId == null || accountId.isBlank()) return null;
        lock.readLock().lock();
        try {
            for (Account a : users) if (accountId.equals(a.getAccountID())) return a;
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

    public Account getUserName(String userName) {
        if (userName == null || userName.isBlank()) return null;
        lock.readLock().lock();
        try {
            for (Account a : users) if (userName.equals(a.getUserName())) return a;
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
