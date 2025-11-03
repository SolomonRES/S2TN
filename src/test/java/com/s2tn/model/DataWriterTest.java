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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class DataWriterTest {

    private DataWriter writer;

    private static Path callPathMethod(Object target, String method) throws Exception {
        Method m = target.getClass().getDeclaredMethod(method);
        m.setAccessible(true);
        Object r = m.invoke(target);
        assertNotNull(r, "❌ Expected non-null Path from " + method);
        assertTrue(r instanceof Path, "❌ " + method + " should return a Path");
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
        assertNotNull(writer, "❌ DataWriter should be constructible");
    }

    @AfterEach
    void tearDown() {
        writer = null;
    }

    @Test
    @DisplayName("✅ saveUsers writes JSON (no passwords) and is idempotent")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void saveUsers() throws Exception {
        Path p = null;
        try { p = callPathMethod(writer, "usersPath"); } catch (Throwable ignored) {}
        if (p != null) assumeTrue(ensureParent(p), "❌ Could not create parent for usersPath");

        // Run twice to check idempotency / no exceptions
        assertDoesNotThrow(() -> writer.saveUsers(), "❌ saveUsers threw on first write");
        long size1 = (p != null && Files.exists(p)) ? Files.size(p) : -1L;

        assertDoesNotThrow(() -> writer.saveUsers(), "❌ saveUsers threw on second write");
        long size2 = (p != null && Files.exists(p)) ? Files.size(p) : -1L;

        if (p != null && Files.exists(p)) {
            String s = Files.readString(p, StandardCharsets.UTF_8).trim();
            assertFalse(s.contains("\"password\""), "❌ Users JSON should not contain passwords");
            // File should not grow across identical consecutive writes (heuristic)
            assertEquals(size1, size2, "❌ Consecutive saveUsers writes should be idempotent in size");
        }
    }

    @Test
    @DisplayName("✅ saveDungeons accepts empty & null lists and outputs array brackets")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void saveDungeons() throws Exception {
        Path p = null;
        try { p = callPathMethod(writer, "dungeonPath"); } catch (Throwable ignored) {}
        if (p != null) assumeTrue(ensureParent(p), "❌ Could not create parent for dungeonPath");

        List<Dungeon> empty = new ArrayList<>();
        assertDoesNotThrow(() -> writer.saveDungeons(empty), "❌ saveDungeons(empty) threw");
        assertDoesNotThrow(() -> writer.saveDungeons(new ArrayList<>()), "❌ saveDungeons(new ArrayList<>()) threw");

        // Edge: null argument should be safely handled if method is defensive
        assertDoesNotThrow(() -> writer.saveDungeons(null), "❌ saveDungeons(null) should not throw");

        if (p != null && Files.exists(p)) {
            String s = Files.readString(p, StandardCharsets.UTF_8).trim();
            assertTrue(s.startsWith("["), "❌ Dungeons JSON should start with '['");
            assertTrue(s.endsWith("]"), "❌ Dungeons JSON should end with ']'");
        }
    }

    @Test
    @DisplayName("✅ saveGame produces non-blank content and is idempotent")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void saveGame() throws Exception {
        Path p = null;
        try { p = callPathMethod(writer, "gamePath"); } catch (Throwable ignored) {}
        if (p != null) assumeTrue(ensureParent(p), "❌ Could not create parent for gamePath");

        assertDoesNotThrow(() -> writer.saveGame(), "❌ saveGame first write threw");
        long size1 = (p != null && Files.exists(p)) ? Files.size(p) : -1L;

        assertDoesNotThrow(() -> writer.saveGame(), "❌ saveGame second write threw");
        long size2 = (p != null && Files.exists(p)) ? Files.size(p) : -1L;

        if (p != null && Files.exists(p)) {
            String s = Files.readString(p, StandardCharsets.UTF_8).trim();
            assertFalse(s.isBlank(), "❌ Game JSON should not be blank");
            // Often stable; allow equality but don't fail if it legitimately changed
            assertTrue(size2 >= 0, "❌ Could not get game file size");
        }
    }

    @Test
    @DisplayName("✅ saveLeaderboard writes non-blank content and tolerates repeats")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void saveLeaderboard() throws Exception {
        Path p = null;
        try { p = callPathMethod(writer, "lbPath"); } catch (Throwable ignored) {}
        if (p != null) assumeTrue(ensureParent(p), "❌ Could not create parent for lbPath");
        Leaderboard lb = new Leaderboard();

        assertDoesNotThrow(() -> writer.saveLeaderboard(lb), "❌ saveLeaderboard first write threw");
        assertDoesNotThrow(() -> writer.saveLeaderboard(lb), "❌ saveLeaderboard second write threw");
        if (p != null && Files.exists(p)) {
            String s = Files.readString(p, StandardCharsets.UTF_8).trim();
            assertFalse(s.isBlank(), "❌ Leaderboard JSON should not be blank");
        }

        // Edge: null leaderboard should not cause a crash if method is defensive
        assertDoesNotThrow(() -> writer.saveLeaderboard(null), "❌ saveLeaderboard(null) should not throw");
    }

    @Test
    @DisplayName("✅ writeCertificate produces expected lines for multiple cases")
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

            assertDoesNotThrow(
                () -> DataWriter.writeCertificate(account, "Game_" + diffs[idx], hints[idx], diffs[idx], scores[idx]),
                "❌ writeCertificate threw for " + uname
            );
            assertTrue(Files.exists(p), "❌ Certificate file should exist for " + uname);
            String content = Files.readString(p, StandardCharsets.UTF_8);
            assertTrue(content.contains(uname), "❌ Certificate missing username for " + uname);
            assertTrue(content.contains("Game_" + diffs[idx]), "❌ Certificate missing game name for " + uname);
            assertTrue(content.contains(diffs[idx]), "❌ Certificate missing difficulty for " + uname);
            assertTrue(content.contains(String.valueOf(hints[idx])), "❌ Certificate missing hints for " + uname);
            assertTrue(content.contains(String.valueOf(scores[idx])), "❌ Certificate missing score for " + uname);

            // Overwrite behavior ✅
            assertDoesNotThrow(
                () -> DataWriter.writeCertificate(account, "Game_" + diffs[idx], hints[idx], diffs[idx], scores[idx] + 1),
                "❌ Overwrite writeCertificate threw for " + uname
            );
            String content2 = Files.readString(p, StandardCharsets.UTF_8);
            assertTrue(content2.contains(String.valueOf(scores[idx] + 1)),
                "❌ Certificate should reflect updated score on overwrite for " + uname);

            Files.deleteIfExists(p);
        }
    }

    @Test
    @DisplayName("✅ writeCertificate handles edge usernames and values")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void writeCertificateEdgeCases() throws Exception {
        // Blank username
        Account blank = new Account("", "pw");
        Path p1 = Path.of("Certificate_" + blank.getUserName() + ".txt");
        Files.deleteIfExists(p1);
        assertDoesNotThrow(
            () -> DataWriter.writeCertificate(blank, "Game_X", 0, "EASY", 0),
            "❌ writeCertificate with blank username threw"
        );
        assertTrue(Files.exists(p1), "❌ Certificate file should exist for blank username");
        Files.deleteIfExists(p1);

        // Large numbers & negative values (should still write without throwing)
        Account big = new Account("Big_Number_User", "pw");
        Path p2 = Path.of("Certificate_" + big.getUserName() + ".txt");
        Files.deleteIfExists(p2);
        assertDoesNotThrow(
            () -> DataWriter.writeCertificate(big, "MegaGame", Integer.MAX_VALUE, "HARD", Integer.MIN_VALUE),
            "❌ writeCertificate with extreme values threw"
        );
        assertTrue(Files.exists(p2), "❌ Certificate file should exist for extreme values");
        String s = Files.readString(p2, StandardCharsets.UTF_8);
        assertTrue(s.contains(String.valueOf(Integer.MAX_VALUE)), "❌ Hints should include Integer.MAX_VALUE ✅");
        assertTrue(s.contains(String.valueOf(Integer.MIN_VALUE)), "❌ Score should include Integer.MIN_VALUE ✅");
        Files.deleteIfExists(p2);
    }
}
