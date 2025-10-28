package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents the leaderboard for tracking player scores and performance.
 * Maintains a list of entries sorted by score, time, and timestamp.
 */
public class Leaderboard {

    /**
     * Represents a single leaderboard entry for a player.
     */
    public static final class Entry {
        private final String userName;
        private final int score;
        private final long elapsedTime; // millis
        private final long timestamp;   // epoch millis when recorded

        /** Creates a leaderboard entry with the given values. */
        public Entry(String userName, int score, long elapsedTime, long timestamp) {
            this.userName = userName;
            this.score = score;
            this.elapsedTime = elapsedTime;
            this.timestamp = timestamp;
        }

        /** Returns the player's username. */
        public String getUserName() { return userName; }

        /** Returns the player's score. */
        public int getScore() { return score; }

        /** Returns the player's elapsed time in milliseconds. */
        public long getElapsedTime() { return elapsedTime; }

        /** Returns the timestamp of when the score was recorded. */
        public long getTimestamp() { return timestamp; }

        /** Returns a string representation of this leaderboard entry. */
        @Override
        public String toString() {
            return "Entry{user=" + userName + ", score=" + score +
                   ", time=" + elapsedTime + "ms, ts=" + timestamp + "}";
        }
    }

    private final List<Entry> entries = new ArrayList<>();

    /** Comparator used to rank entries by score, elapsed time, and timestamp. */
    private static final Comparator<Entry> RANKING =
        Comparator.comparingInt(Entry::getScore).reversed()
                  .thenComparingLong(Entry::getElapsedTime)
                  .thenComparingLong(Entry::getTimestamp);

    /**
     * Submits a new score for a user. Updates their record if the new score is higher.
     *
     * @param userName the player's username
     * @param score the score achieved
     * @param elapsedTime the time taken in milliseconds
     */
    public synchronized void submit(String userName, int score, long elapsedTime) {
        if (userName == null || userName.isBlank()) return;
        if (score < 0) score = 0;
        if (elapsedTime < 0) elapsedTime = 0;

        int idx = -1;
        for (int i = 0; i < entries.size(); i++) {
            if (Objects.equals(entries.get(i).getUserName(), userName)) { idx = i; break; }
        }

        long now = System.currentTimeMillis();
        Entry candidate = new Entry(userName, score, elapsedTime, now);

        if (idx < 0) {
            entries.add(candidate);
        } else {
            Entry current = entries.get(idx);
            int cmp = RANKING.compare(candidate, current);
            if (cmp < 0) {
                // keep current score
            } else if (cmp > 0) {
                entries.set(idx, candidate);
            } else {
                // equal performance, no update
            }
        }

        Account acc = new UserService().getByUserName(userName);
        if (acc != null && acc.getScore() < score) {
            acc.setScore(score);
            new DataWriter().saveUsers(); // keep users.json up to date
        }
    }

    /**
     * Returns the top N leaderboard entries sorted by ranking.
     *
     * @param n number of top entries to return
     * @return list of top leaderboard entries
     */
    public synchronized List<Entry> topScore(int n) {
        if (n <= 0) return List.of();
        return entries.stream()
                .sorted(RANKING)
                .limit(n)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns the top N player accounts sorted by score.
     *
     * @param n number of top players to return
     * @return list of top player accounts
     */
    public List<Account> getTopPlayers(int n) {
        return UserList.getInstance().getAll().stream()
            .sorted(Comparator.comparingInt(Account::getScore).reversed())
            .limit(Math.max(0, n))
            .collect(Collectors.toList());
    }

    /** Clears all entries from the leaderboard. */
    public synchronized void clear() { entries.clear(); }
}
