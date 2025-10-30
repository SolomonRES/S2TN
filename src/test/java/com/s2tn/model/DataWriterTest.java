package com.s2tn.model;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class DataWriterTest {

    private DataWriter writer;

    private static Path callPathMethod(Object target, String method) throws Exception {
        Method m = target.getClass().getDeclaredMethod(method);
        m.setAccessible(true);
        Object r = m.invoke(target);
        assertNotNull(r);
        assertTrue(r instanceof Path);
        return (Path) r;
    }

    private static boolean ensureParent(Path p) {
        try {
            Path parent = p.getParent();
            if (parent != null) Files.createDirectories(parent);
            return parent == null || Files.isDirectory(parent);
        } catch (Exception e) {
            return false;
        }
    }

    @BeforeEach
    void setUp() {
        writer = new DataWriter();
        assertNotNull(writer);
    }

    @AfterEach
    void tearDown() {
        writer = null;
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void saveUsers() throws Exception {
        Path p = null;
        try { p = callPathMethod(writer, "usersPath"); } catch (Throwable ignored) {}
        if (p != null) assumeTrue(ensureParent(p));
        assertDoesNotThrow(() -> writer.saveUsers());
        assertDoesNotThrow(() -> writer.saveUsers());
        if (p != null && Files.exists(p)) {
            String s = Files.readString(p, StandardCharsets.UTF_8).trim();
            assertFalse(s.contains("\"password\""));
        }
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void saveDungeons() throws Exception {
        Path p = null;
        try { p = callPathMethod(writer, "dungeonPath"); } catch (Throwable ignored) {}
        if (p != null) assumeTrue(ensureParent(p));
        List<Dungeon> empty = new ArrayList<>();
        assertDoesNotThrow(() -> writer.saveDungeons(empty));
        assertDoesNotThrow(() -> writer.saveDungeons(new ArrayList<>()));
        if (p != null && Files.exists(p)) {
            String s = Files.readString(p, StandardCharsets.UTF_8).trim();
            assertTrue(s.startsWith("["));
            assertTrue(s.endsWith("]"));
        }
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void saveGame() throws Exception {
        Path p = null;
        try { p = callPathMethod(writer, "gamePath"); } catch (Throwable ignored) {}
        if (p != null) assumeTrue(ensureParent(p));
        assertDoesNotThrow(() -> writer.saveGame());
        assertDoesNotThrow(() -> writer.saveGame());
        if (p != null && Files.exists(p)) {
            String s = Files.readString(p, StandardCharsets.UTF_8).trim();
            assertFalse(s.isBlank());
        }
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void saveLeaderboard() throws Exception {
        Path p = null;
        try { p = callPathMethod(writer, "lbPath"); } catch (Throwable ignored) {}
        if (p != null) assumeTrue(ensureParent(p));
        Leaderboard lb = new Leaderboard();
        assertDoesNotThrow(() -> writer.saveLeaderboard(lb));
        assertDoesNotThrow(() -> writer.saveLeaderboard(lb));
        if (p != null && Files.exists(p)) {
            String s = Files.readString(p, StandardCharsets.UTF_8).trim();
            assertFalse(s.isBlank());
        }
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void writeCertificate() throws Exception {
        String[] diffs = {"EASY", "NORMAL", "HARD"};
        int[] hints = {0, 2, 5};
        int[] scores = {100, 250, 500};
        for (int i = 0; i < diffs.length; i++) {
            final int idx = i;
            String uname = "User_" + diffs[idx] + "_" + System.nanoTime();
            Account account = new Account(uname, "x");
            Path p = Path.of("Certificate_" + account.getUserName() + ".txt");
            Files.deleteIfExists(p);
            assertDoesNotThrow(() -> DataWriter.writeCertificate(account, "Game_" + diffs[idx], hints[idx], diffs[idx], scores[idx]));
            assertTrue(Files.exists(p));
            String content = Files.readString(p, StandardCharsets.UTF_8);
            assertTrue(content.contains(uname));
            assertTrue(content.contains("Game_" + diffs[idx]));
            assertTrue(content.contains(diffs[idx]));
            assertTrue(content.contains(String.valueOf(hints[idx])));
            assertTrue(content.contains(String.valueOf(scores[idx])));
            Files.deleteIfExists(p);
        }
    }
}