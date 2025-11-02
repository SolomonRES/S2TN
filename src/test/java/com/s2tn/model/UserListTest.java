package com.s2tn.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserListTest {

    private UserList userList;
    private Account user1;
    private Account user2;

    @BeforeEach
    void setUp() {
        userList = UserList.getInstance();
        userList.replaceAll(new ArrayList<>()); // Clear the list for test isolation

        user1 = new Account("userOne", "pass1");
        user2 = new Account("userTwo", "pass2");
    }

    @AfterEach
    void tearDown() {
        userList.replaceAll(new ArrayList<>());
    }

    @Test
    @DisplayName("getInstance should return a non-null singleton instance")
    void getInstance() {
        assertNotNull(userList, "getInstance() should not return null.");
        UserList anotherInstance = UserList.getInstance();
        assertSame(userList, anotherInstance, "getInstance() should always return the same instance.");
    }

    @Test
    @DisplayName("getAll should return an unmodifiable list of users")
    void getAll() {
        userList.addUser(user1);
        List<Account> allUsers = userList.getAll();
        assertEquals(1, allUsers.size());
        assertThrows(UnsupportedOperationException.class, () -> allUsers.add(user2), "The list returned by getAll() should be unmodifiable.");
    }

    @Test
    @DisplayName("replaceAll should replace all users in the list")
    void replaceAll() {
        userList.addUser(user1);
        assertEquals(1, userList.getAll().size());

        List<Account> newList = new ArrayList<>();
        newList.add(user2);
        userList.replaceAll(newList);

        assertEquals(1, userList.getAll().size());
        assertEquals("userTwo", userList.getAll().get(0).getUserName());

        userList.replaceAll(null);
        assertTrue(userList.getAll().isEmpty(), "Replacing with null should clear the list.");
    }

    @Test
    @DisplayName("getId should retrieve a user by their account ID")
    void getId() {
        userList.addUser(user1);
        Account found = userList.getId(user1.getAccountID());
        assertSame(user1, found);

        assertNull(userList.getId(UUID.randomUUID().toString()), "Should return null for a non-existent ID.");
        assertNull(userList.getId(null), "Should return null for a null ID.");
        assertNull(userList.getId(""), "Should return null for a blank ID.");
    }

    @Test
    @DisplayName("getUser should retrieve a user by their username")
    void getUser() {
        userList.addUser(user1);
        Account found = userList.getUser("userOne");
        assertSame(user1, found);

        assertNull(userList.getUser("nonExistent"), "Should return null for a non-existent username.");
        assertNull(userList.getUser(null), "Should return null for a null username.");
        assertNull(userList.getUser("  "), "Should return null for a blank username.");
    }

    @Test
    @DisplayName("getUserName should be an alias for getUser")
    void getUserName() {
        userList.addUser(user1);
        Account found = userList.getUserName("userOne");
        assertSame(user1, found);
        assertNull(userList.getUserName("nonExistent"));
    }

    @Test
    @DisplayName("addUser should add a new user and prevent duplicates")
    void addUser() {
        assertTrue(userList.addUser(user1), "Should successfully add a new user.");
        assertEquals(1, userList.getAll().size());

        assertFalse(userList.addUser(user1), "Should not add the same user instance again.");
        assertEquals(1, userList.getAll().size());

        Account duplicateName = new Account("userOne", "differentPass");
        assertFalse(userList.addUser(duplicateName), "Should not add a user with a duplicate username.");

        Account duplicateId = new Account("anotherUser", "pass");
        duplicateId.setAccountID(user1.getAccountID());
        assertFalse(userList.addUser(duplicateId), "Should not add a user with a duplicate ID.");

        assertFalse(userList.addUser(null), "Should not add a null user.");
    }

    @Test
    @DisplayName("addUser should auto-generate an ID if one is not present")
    void addUser_autoGeneratesId() {
        Account userWithNullId = new Account("newUser", "pass");
        userWithNullId.setAccountID(null); // Explicitly set to null

        assertTrue(userList.addUser(userWithNullId));
        assertNotNull(userWithNullId.getAccountID());
        assertFalse(userWithNullId.getAccountID().isBlank());
    }

    @Test
    @DisplayName("removeUser should remove a user by their account ID")
    void removeUser() {
        userList.addUser(user1);
        assertEquals(1, userList.getAll().size());

        assertTrue(userList.removeUser(user1.getAccountID()), "Should return true on successful removal.");
        assertEquals(0, userList.getAll().size());

        assertFalse(userList.removeUser(user2.getAccountID()), "Should return false for a non-existent user.");
        assertFalse(userList.removeUser(null), "Should return false for a null ID.");
    }

    @Test
    @DisplayName("updateUser should modify an existing user")
    void updateUser() {
        userList.addUser(user1);
        user1.setScore(100);

        assertTrue(userList.updateUser(user1), "Should successfully update a user.");
        assertEquals(100, userList.getUser("userOne").getScore());

        assertFalse(userList.updateUser(user2), "Should not update a user that is not in the list.");

        assertFalse(userList.updateUser(null), "Should not update a null user.");
    }

    @Test
    @DisplayName("updateUser should prevent username conflicts")
    void updateUser_preventUsernameConflict() {
        userList.addUser(user1);
        userList.addUser(user2);

        // Try to update user2 to have user1's name
        user2.setUserName("userOne");
        assertFalse(userList.updateUser(user2), "Should prevent updating to a username that is already taken.");

        // The original user2 should remain unchanged in the list
        assertEquals("userTwo", userList.getId(user2.getAccountID()).getUserName());
    }
}