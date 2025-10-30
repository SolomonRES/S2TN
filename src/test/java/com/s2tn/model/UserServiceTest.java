package com.s2tn.model;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    private UserService userService;
    private UserList userList;
    private Account user1;
    private Account user2;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        userList = UserList.getInstance();
        userList.replaceAll(null); // Clear the singleton for test isolation

        user1 = new Account("userOne", "pass1");
        user2 = new Account("userTwo", "pass2");
    }

    @AfterEach
    void tearDown() {
        userList.replaceAll(null);
    }

    @Test
    @DisplayName("addUser should add a new user and prevent duplicates")
    void addUser() {
        assertTrue(userService.addUser(user1), "Should successfully add a new user.");
        assertEquals(1, userList.getAll().size());

        assertFalse(userService.addUser(user1), "Should not add the same user instance again.");

        Account duplicateNameUser = new Account("userOne", "differentPass");
        assertFalse(userService.addUser(duplicateNameUser), "Should not add a user with a duplicate username.");

        assertFalse(userService.addUser(null), "Should not add a null user.");
    }

    @Test
    @DisplayName("updateUser should modify an existing user's data")
    void updateUser() {
        userService.addUser(user1);
        user1.setScore(150);

        assertTrue(userService.updateUser(user1), "Should successfully update a user.");
        assertEquals(150, userList.getUser("userOne").getScore());

        assertFalse(userService.updateUser(user2), "Should not update a user that is not in the list.");
    }

    @Test
    @DisplayName("removeUser should remove a user by their account ID")
    void removeUser() {
        userService.addUser(user1);
        assertEquals(1, userList.getAll().size());

        assertTrue(userService.removeUser(user1.getAccountID()), "Should return true on successful removal.");
        assertEquals(0, userList.getAll().size());

        assertFalse(userService.removeUser(user2.getAccountID()), "Should return false for a non-existent user.");
    }

    @Test
    @DisplayName("listUsers should return all users in the list")
    void listUsers() {
        assertTrue(userService.listUsers().isEmpty(), "List should be empty initially.");

        userService.addUser(user1);
        userService.addUser(user2);

        List<Account> users = userService.listUsers();
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    @DisplayName("getById should retrieve a user by their account ID")
    void getById() {
        userService.addUser(user1);

        Account found = userService.getById(user1.getAccountID());
        assertSame(user1, found, "Should find the correct user by ID.");

        Account notFound = userService.getById(user2.getAccountID());
        assertNull(notFound, "Should return null for a non-existent ID.");

        assertNull(userService.getById(null), "Should return null for a null ID.");
    }

    @Test
    @DisplayName("getByUserName should retrieve a user by their username")
    void getByUserName() {
        userService.addUser(user1);

        Account found = userService.getByUserName("userOne");
        assertSame(user1, found, "Should find the correct user by username.");

        Account notFound = userService.getByUserName("userTwo");
        assertNull(notFound, "Should return null for a non-existent username.");

        assertNull(userService.getByUserName(null), "Should return null for a null username.");
    }
}