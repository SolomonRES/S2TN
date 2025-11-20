package com.s2tn.model;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LeaderboardTest {

    private Leaderboard leaderboard;
    private UserList userList; // To manage accounts for getTopPlayers and submit

    @BeforeEach
    void setUp() {
        leaderboard = new Leaderboard();
        leaderboard.clear(); // Ensure a clean state for the leaderboard
        userList = UserList.getInstance();
        userList.replaceAll(new ArrayList<>()); // Ensure UserList is clean
    }

    @AfterEach
    void tearDown() {
        userList.replaceAll(new ArrayList<>()); // Clean up UserList after each test
        leaderboard.clear();
        leaderboard = null;
        userList = null;
    }

    // --- Leaderboard.Entry Tests ---
    @Test
    @DisplayName("Leaderboard.Entry constructor and getters should work correctly")
    void testEntryConstructorAndGetters() {
        String userName = "testUser";
        int score = 100;
        long elapsedTime = 5000;
        long timestamp = System.currentTimeMillis();

        Leaderboard.Entry entry = new Leaderboard.Entry(userName, score, elapsedTime, timestamp);

        assertEquals(userName, entry.getUserName());
        assertEquals(score, entry.getScore());
        assertEquals(elapsedTime, entry.getElapsedTime());
        assertEquals(timestamp, entry.getTimestamp());
    }

    @Test
    @DisplayName("Leaderboard.Entry toString should return a formatted string")
    void testEntryToString() {
        Leaderboard.Entry entry = new Leaderboard.Entry("user1", 100, 5000, 123456789L);
        String expected = "Entry{user=user1, score=100, time=5000ms, ts=123456789}";
        assertEquals(expected, entry.toString());
    }

    // --- Leaderboard Tests ---

    @Test
    @DisplayName("submit should add a new entry to the leaderboard")
    void submit() {
        leaderboard.submit("player1", 100, 60000);
        List<Leaderboard.Entry> top = leaderboard.topScore(1);
        assertEquals(1, top.size());
        assertEquals("player1", top.get(0).getUserName());
        assertEquals(100, top.get(0).getScore());
    }

    @Test
    @DisplayName("submit should update an existing entry with a higher score")
    void submit_updateHigherScore() {
        leaderboard.submit("player1", 100, 60000);
        leaderboard.submit("player1", 150, 50000); // Higher score, lower time

        List<Leaderboard.Entry> top = leaderboard.topScore(1);
        assertEquals(1, top.size());
        assertEquals("player1", top.get(0).getUserName());
        assertEquals(150, top.get(0).getScore());
        assertEquals(50000, top.get(0).getElapsedTime());
    }

    @Test
    @DisplayName("submit should not update an existing entry with a lower score")
    void submit_updateLowerScore() {
        leaderboard.submit("player1", 100, 60000);
        leaderboard.submit("player1", 50, 30000); // Lower score

        List<Leaderboard.Entry> top = leaderboard.topScore(1);
        assertEquals(1, top.size());
        assertEquals("player1", top.get(0).getUserName());
        assertEquals(100, top.get(0).getScore());
        assertEquals(60000, top.get(0).getElapsedTime());
    }

    @Test
    @DisplayName("submit should update an existing entry with same score but lower time")
    void submit_updateSameScoreLowerTime() {
        leaderboard.submit("player1", 100, 60000);
        leaderboard.submit("player1", 100, 30000); // Same score, lower time

        List<Leaderboard.Entry> top = leaderboard.topScore(1);
        assertEquals(1, top.size());
        assertEquals("player1", top.get(0).getUserName());
        assertEquals(100, top.get(0).getScore());
        assertEquals(30000, top.get(0).getElapsedTime());
    }

    @Test
    @DisplayName("submit should not update an existing entry with same score and higher time")
    void submit_updateSameScoreHigherTime() {
        leaderboard.submit("player1", 100, 30000);
        leaderboard.submit("player1", 100, 60000); // Same score, higher time

        List<Leaderboard.Entry> top = leaderboard.topScore(1);
        assertEquals(1, top.size());
        assertEquals("player1", top.get(0).getUserName());
        assertEquals(100, top.get(0).getScore());
        assertEquals(30000, top.get(0).getElapsedTime());
    }

    @Test
    @DisplayName("submit should handle null username by ignoring the submission")
    void submit_nullUsername() {
        leaderboard.submit(null, 100, 60000);
        assertTrue(leaderboard.topScore(1).isEmpty());
    }

    @Test
    @DisplayName("submit should handle blank username by ignoring the submission")
    void submit_blankUsername() {
        leaderboard.submit("  ", 100, 60000);
        assertTrue(leaderboard.topScore(1).isEmpty());
    }

    @Test
    @DisplayName("submit should clamp negative score to 0")
    void submit_negativeScore() {
        leaderboard.submit("player1", -10, 60000);
        assertEquals(0, leaderboard.topScore(1).get(0).getScore());
    }

    @Test
    @DisplayName("submit should clamp negative elapsed time to 0")
    void submit_negativeElapsedTime() {
        leaderboard.submit("player1", 100, -5000);
        assertEquals(0, leaderboard.topScore(1).get(0).getElapsedTime());
    }

    @Test
    @DisplayName("submit should update account score in UserList if new score is higher")
    void submit_updateAccountScore() {
        Account acc = new Account("player1", "pass");
        userList.addUser(acc);
        assertEquals(0, acc.getScore());

        leaderboard.submit("player1", 100, 60000);
        assertEquals(100, userList.getUser("player1").getScore());

        leaderboard.submit("player1", 50, 30000); // Lower score, should not update
        assertEquals(100, userList.getUser("player1").getScore());

        leaderboard.submit("player1", 150, 50000); // Higher score, should update
        assertEquals(150, userList.getUser("player1").getScore());
    }

    @Test
    @DisplayName("topScore should return an empty list for n <= 0")
    void topScore() {
        leaderboard.submit("player1", 100, 60000);
        assertTrue(leaderboard.topScore(0).isEmpty());
        assertTrue(leaderboard.topScore(-5).isEmpty());
    }

    @Test
    @DisplayName("topScore should return the correct number of top entries")
    void topScore_correctCount() {
        leaderboard.submit("player1", 100, 60000);
        leaderboard.submit("player2", 200, 50000);
        leaderboard.submit("player3", 50, 70000);

        List<Leaderboard.Entry> top2 = leaderboard.topScore(2);
        assertEquals(2, top2.size());
        assertEquals("player2", top2.get(0).getUserName()); // Score 200
        assertEquals("player1", top2.get(1).getUserName()); // Score 100
    }

    @Test
    @DisplayName("topScore should return entries sorted by score (desc), then time (asc), then timestamp (asc)")
    void topScore_sortingOrder() {
        // Same score, different times
        leaderboard.submit("playerA", 100, 60000); // 2nd best time
        leaderboard.submit("playerB", 100, 30000); // Best time
        leaderboard.submit("playerC", 100, 90000); // Worst time

        // Different scores
        leaderboard.submit("playerD", 200, 10000); // Best score
        leaderboard.submit("playerE", 50, 120000); // Worst score

        List<Leaderboard.Entry> top5 = leaderboard.topScore(5);
        assertEquals(5, top5.size());

        // Expected order: D (200), B (100, 30k), A (100, 60k), C (100, 90k), E (50)
        assertEquals("playerD", top5.get(0).getUserName());
        assertEquals("playerB", top5.get(1).getUserName());
        assertEquals("playerA", top5.get(2).getUserName());
        assertEquals("playerC", top5.get(3).getUserName());
        assertEquals("playerE", top5.get(4).getUserName());

        // Test same score, same time, different timestamp (should be stable or by timestamp)
        leaderboard.clear();
        long ts1 = System.currentTimeMillis();
        leaderboard.submit("playerX", 100, 50000);
        try { Thread.sleep(10); } catch (InterruptedException e) { /* ignore */ }
        long ts2 = System.currentTimeMillis();
        leaderboard.submit("playerY", 100, 50000);

        List<Leaderboard.Entry> top2Same = leaderboard.topScore(2);
        assertEquals(2, top2Same.size());
        // The order for equal score/time is by timestamp (earlier timestamp first)
        // This is implicitly handled by the Comparator.comparingLong(Entry::getTimestamp)
        assertEquals("playerX", top2Same.get(0).getUserName());
        assertEquals("playerY", top2Same.get(1).getUserName());
    }

    @Test
    @DisplayName("topScore should return an unmodifiable list")
    void topScore_unmodifiableList() {
        leaderboard.submit("player1", 100, 60000);
        List<Leaderboard.Entry> top = leaderboard.topScore(1);
        assertThrows(UnsupportedOperationException.class, () -> top.add(null));
    }

    @Test
    @DisplayName("getTopPlayers should return an empty list for n <= 0")
    void getTopPlayers() {
        userList.addUser(new Account("player1", "pass"));
        assertTrue(leaderboard.getTopPlayers(0).isEmpty());
        assertTrue(leaderboard.getTopPlayers(-5).isEmpty());
    }

    @Test
    @DisplayName("getTopPlayers should return the correct number of top accounts sorted by score (desc)")
    void getTopPlayers_sortingAndCount() {
        Account acc1 = new Account("player1", "pass"); acc1.setScore(100);
        Account acc2 = new Account("player2", "pass"); acc2.setScore(200);
        Account acc3 = new Account("player3", "pass"); acc3.setScore(50);

        userList.addUser(acc1);
        userList.addUser(acc2);
        userList.addUser(acc3);

        List<Account> top2 = leaderboard.getTopPlayers(2);
        assertEquals(2, top2.size());
        assertEquals("player2", top2.get(0).getUserName());
        assertEquals("player1", top2.get(1).getUserName());

        List<Account> topAll = leaderboard.getTopPlayers(5); // Request more than available
        assertEquals(3, topAll.size());
        assertEquals("player2", topAll.get(0).getUserName());
        assertEquals("player1", topAll.get(1).getUserName());
        assertEquals("player3", topAll.get(2).getUserName());
    }

    @Test
    @DisplayName("clear should remove all entries from the leaderboard")
    void clear() {
        leaderboard.submit("player1", 100, 60000);
        leaderboard.submit("player2", 200, 50000);
        assertFalse(leaderboard.topScore(1).isEmpty());

        leaderboard.clear();
        assertTrue(leaderboard.topScore(1).isEmpty());
    }
}