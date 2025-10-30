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

class DataLoaderTest {

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
        if (threwOnMutate) {
            assertEquals(baseline.size(), fresh.size());
        } else {
            assertEquals(baseline.size(), fresh.size());
            assertFalse(fresh.contains(null));
        }
        List<Dungeon> a = loader.getDungeons();
        List<Dungeon> b = loader.getDungeons();
        assertNotSame(a, b);
    }
}