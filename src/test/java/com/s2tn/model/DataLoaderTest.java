package com.s2tn.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;

class DataLoaderTest {

    // ===== Emoji PASS/FAIL logging =====
    @RegisterExtension
    static final EmojiWatcher EMOJI_WATCHER = new EmojiWatcher();

    static class EmojiWatcher implements TestWatcher {
        private static final String GREEN = "\u001B[32m";
        private static final String RED   = "\u001B[31m";
        private static final String YEL   = "\u001B[33m";
        private static final String CYAN  = "\u001B[36m";
        private static final String RESET = "\u001B[0m";

        @Override
        public void testSuccessful(ExtensionContext ctx) {
            System.out.println(GREEN + "✅ PASS " + RESET + format(ctx));
        }

        @Override
        public void testFailed(ExtensionContext ctx, Throwable cause) {
            System.out.println(RED + "❌ FAIL " + RESET + format(ctx) + " — " + cause.getMessage());
        }

        @Override
        public void testAborted(ExtensionContext ctx, Throwable cause) {
            System.out.println(YEL + "⏭️  SKIP " + RESET + format(ctx) + (cause != null ? " — " + cause.getMessage() : ""));
        }

        @Override
        public void testDisabled(ExtensionContext ctx, java.util.Optional<String> reason) {
            System.out.println(CYAN + "🚧 DISABLED " + RESET + format(ctx) + reason.map(r -> " — " + r).orElse(""));
        }

        private String format(ExtensionContext ctx) {
            String cls = ctx.getRequiredTestClass().getSimpleName();
            String name = ctx.getDisplayName(); // defaults to method name if no @DisplayName
            return cls + "." + name;
        }
    }
    // ===== end logging =====

    private DataLoader loader;

    @BeforeEach
    void setUp() {
        loader = new DataLoader();
        assertNotNull(loader);
    }

    @AfterEach
    void tearDown() {
        loader = null;
    }

    @Test
    void loadUsers() {
        assertDoesNotThrow(() -> loader.loadUsers());
        assertDoesNotThrow(() -> loader.loadUsers());
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void loadDungeons() throws InterruptedException {
        assertDoesNotThrow(() -> loader.loadDungeons());

        List<Dungeon> first = new ArrayList<>(loader.getDungeons());
        assertNotNull(first);
        assertFalse(first.contains(null));

        assertDoesNotThrow(() -> loader.loadDungeons());

        List<Dungeon> second = new ArrayList<>(loader.getDungeons());
        assertNotNull(second);
        assertFalse(second.contains(null));

        if (!first.isEmpty()) {
            assertEquals(first.size(), second.size());
        } else {
            assertTrue(second.isEmpty());
        }

        int threads = Math.max(2, Runtime.getRuntime().availableProcessors());
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    assertDoesNotThrow(() -> loader.loadDungeons());
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        boolean finished = done.await(2, TimeUnit.SECONDS);
        pool.shutdownNow();
        assertTrue(finished);

        List<Dungeon> afterConcurrent = loader.getDungeons();
        assertNotNull(afterConcurrent);
        assertFalse(afterConcurrent.contains(null));
    }

    @Test
    void getDungeons() {
        List<Dungeon> before = loader.getDungeons();
        assertNotNull(before);

        assertDoesNotThrow(() -> loader.loadDungeons());
        List<Dungeon> after = loader.getDungeons();
        assertNotNull(after);
        assertFalse(after.contains(null));

        List<Dungeon> baseline = new ArrayList<>(after);
        List<Dungeon> returned = loader.getDungeons();
        assertNotNull(returned);

        boolean threwOnMutate = false;
        try {
            returned.add(null);
        } catch (UnsupportedOperationException uoe) {
            threwOnMutate = true;
        } catch (Exception ex) {
            threwOnMutate = true;
        }

        List<Dungeon> fresh = loader.getDungeons();
        assertNotNull(fresh);
        assertEquals(baseline.size(), fresh.size());
        if (!threwOnMutate) {
            assertFalse(fresh.contains(null));
        }

        List<Dungeon> a = loader.getDungeons();
        List<Dungeon> b = loader.getDungeons();
        assertNotSame(a, b);
    }
}
