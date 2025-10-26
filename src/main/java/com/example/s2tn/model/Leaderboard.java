package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Leaderboard {


    public static final class Entry {
        private final String userName;
        private final int score;
        private final long elapsedTime; // millis
        private final long timestamp;   // epoch millis when recorded

        public Entry(String userName, int score, long elapsedTime, long timestamp) {
            this.userName = userName;
            this.score = score;
            this.elapsedTime = elapsedTime;
            this.timestamp = timestamp;
        }

        public String getUserName() { return userName; }
        public int getScore() { return score; }
        public long getElapsedTime() { return elapsedTime; }
        public long getTimestamp() { return timestamp; }

        @Override public String toString() {
            return "Entry{user=" + userName + ", score=" + score +
                   ", time=" + elapsedTime + "ms, ts=" + timestamp + "}";
        }
    }

    private final List<Entry> entries = new ArrayList<>();

    private static final Comparator<Entry> RANKING =
        Comparator.comparingInt(Entry::getScore).reversed()
                  .thenComparingLong(Entry::getElapsedTime)
                  .thenComparingLong(Entry::getTimestamp);

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
            } else if (cmp > 0) {
                entries.set(idx, candidate);
            } else {

            }
        }

        Account acc = new UserService().getByUserName(userName);
        if (acc != null && acc.getScore() < score) {
            acc.setScore(score);
            new DataWriter().saveUsers(); // keep users.json up to date
        }
    }

    public synchronized List<Entry> topScore(int n) {
        if (n <= 0) return List.of();
        return entries.stream()
                .sorted(RANKING)
                .limit(n)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Account> getTopPlayers(int n) {
        return UserList.getInstance().getAll().stream()
            .sorted(Comparator.comparingInt(Account::getScore).reversed())
            .limit(Math.max(0, n))
            .collect(Collectors.toList());
    }

    public synchronized void clear() { entries.clear(); }
}
