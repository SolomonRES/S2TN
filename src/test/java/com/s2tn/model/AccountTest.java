package com.s2tn.model;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account("testUser", "password123");
    }

    @AfterEach
    void tearDown() {
        account = null;
    }

    @Test
    @DisplayName("Default constructor should create an empty account")
    void testDefaultConstructor() {
        Account emptyAccount = new Account();
        assertNotNull(emptyAccount.getAccountID());
        assertFalse(emptyAccount.getAccountID().isEmpty());
        assertEquals("", emptyAccount.getUserName());
        assertEquals("", emptyAccount.getPassword());
        assertEquals(0, emptyAccount.getScore());
        assertEquals(0, emptyAccount.getRank());
        assertTrue(emptyAccount.getAchievements().isEmpty());
    }

    @Test
    @DisplayName("Constructor with username and password should set properties correctly")
    void testConstructorWithParameters() {
        assertEquals("testUser", account.getUserName());
        assertEquals("password123", account.getPassword());
        assertNotNull(account.getAccountID());
        assertEquals(0, account.getScore());
        assertEquals(0, account.getRank());
        assertTrue(account.getAchievements().isEmpty());
    }

    @Test
    @DisplayName("Constructor should handle null and whitespace inputs")
    void testConstructorWithNullAndWhitespace() {
        Account account1 = new Account(null, null);
        assertEquals("", account1.getUserName(), "Null username should result in an empty string.");
        assertEquals("", account1.getPassword(), "Null password should result in an empty string.");

        Account account2 = new Account("  user  ", "  pass  ");
        assertEquals("user", account2.getUserName());
        assertEquals("pass", account2.getPassword());
    }

    @Test
    @DisplayName("login should succeed with correct credentials")
    void login() {
        assertTrue(account.login("testUser", "password123"), "Login should succeed with correct case.");
        assertTrue(account.login("TESTUSER", "password123"), "Login should succeed with different case for username.");
    }

    @Test
    @DisplayName("login should fail with incorrect credentials")
    void login_shouldFailWithIncorrectCredentials() {
        assertFalse(account.login("wrongUser", "password123"), "Login should fail with incorrect username.");
        assertFalse(account.login("testUser", "wrongPassword"), "Login should fail with incorrect password.");
        assertFalse(account.login("testUser", "Password123"), "Login should fail with incorrect password case.");
    }

    @Test
    @DisplayName("login should fail with null inputs")
    void login_shouldFailWithNulls() {
        assertFalse(account.login(null, "password123"), "Login should fail with null username.");
        assertFalse(account.login("testUser", null), "Login should fail with null password.");
        assertFalse(account.login(null, null), "Login should fail with both inputs as null.");
    }

    @Test
    @DisplayName("updateScore should correctly add and subtract points")
    void updateScore() {
        account.setScore(100);

        account.updateScore(50);
        assertEquals(150, account.getScore(), "Score should be incremented by 50.");

        account.updateScore(-70);
        assertEquals(80, account.getScore(), "Score should be decremented by 70.");

        account.updateScore(0);
        assertEquals(80, account.getScore(), "Score should not change when 0 points are added.");
    }

    @Test
    @DisplayName("updateScore should not allow score to go below zero")
    void updateScore_shouldNotGoBelowZero() {
        account.setScore(50);
        account.updateScore(-100);
        assertEquals(0, account.getScore(), "Score should be clamped to 0 and not become negative.");
    }

    @Test
    @DisplayName("updateScore should handle integer overflow by capping at MAX_VALUE")
    void updateScore_shouldHandleOverflow() {
        account.setScore(Integer.MAX_VALUE - 10);
        account.updateScore(20);
        assertEquals(Integer.MAX_VALUE, account.getScore(), "Score should be capped at Integer.MAX_VALUE.");
    }

    @Test
    @DisplayName("setScore should not allow negative values")
    void setScore() {
        account.setScore(500);
        assertEquals(500, account.getScore());

        account.setScore(-100);
        assertEquals(0, account.getScore(), "Score should be set to 0 if a negative value is provided.");
    }

    @Test
    @DisplayName("setRank should not allow negative values")
    void setRank() {
        account.setRank(10);
        assertEquals(10, account.getRank());

        account.setRank(-5);
        assertEquals(0, account.getRank(), "Rank should be set to 0 if a negative value is provided.");
    }

    @Test
    @DisplayName("setAccountID should handle valid and invalid UUIDs")
    void setAccountID() {
        String validUUID = UUID.randomUUID().toString();
        account.setAccountID(validUUID);
        assertEquals(validUUID, account.getAccountID());

        String oldID = account.getAccountID();
        account.setAccountID("invalid-uuid");
        assertNotEquals(oldID, account.getAccountID(), "A new UUID should be generated for an invalid ID.");
        assertNotEquals("invalid-uuid", account.getAccountID());
    }

    @Test
    @DisplayName("addAchievements should add unique achievements and return a copy")
    void addAndGetAchievements() {
        // Corrected Achievement instantiation to match Achievement.java constructor
        Achievement achievement1 = new Achievement("First Step", "You started the game.", UUID.randomUUID(), UUID.randomUUID(), 10);
        Achievement achievement2 = new Achievement("Winner", "You won the game.", UUID.randomUUID(), UUID.randomUUID(), 50);

        account.addAchievements(achievement1);
        assertTrue(account.getAchievements().contains(achievement1));
        assertEquals(1, account.getAchievements().size());

        account.addAchievements(achievement1); // Add duplicate
        assertEquals(1, account.getAchievements().size(), "Duplicate achievements should not be added.");

        account.addAchievements(achievement2);
        assertEquals(2, account.getAchievements().size());

        List<Achievement> achievementsCopy = account.getAchievements();
        achievementsCopy.clear();
        assertEquals(2, account.getAchievements().size(), "Modifying the returned list should not affect the account's list.");
    }
}