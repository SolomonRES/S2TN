package com.s2tn.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimerTest {

    private Timer timer;
    private static final long SLEEP_DURATION = 100;
    private static final long TOLERANCE = 50; // ms tolerance for thread scheduling

    @BeforeEach
    void setUp() {
        timer = new Timer();
    }

    @Test
    @DisplayName("A new timer should be in a stopped state")
    void newTimerState() {
        assertFalse(timer.isRunning(), "A new timer should not be running.");
        assertEquals(0, timer.elapsedTime(), "A new timer should have zero elapsed time.");
    }

    @Test
    @DisplayName("start() should set the timer to a running state and reset times")
    void start() {
        timer.start();
        assertTrue(timer.isRunning(), "Timer should be running after start().");

        // Restarting the timer should reset the start time
        long firstStartTime = timer.elapsedTime();
        try { Thread.sleep(SLEEP_DURATION); } catch (InterruptedException e) { fail("Test interrupted"); }
        timer.start();
        long secondStartTime = timer.elapsedTime(); // Also negative
        assertTrue(firstStartTime > 0, "Time should not be negative");
        assertTrue(secondStartTime > firstStartTime, "Restarting the timer should reset its start time.");
    }

    @Test
    @DisplayName("stop() should set the timer to a not-running state")
    void stop() {
        timer.start();
        assertTrue(timer.isRunning(), "Timer should be running before stop().");
        timer.stop();
        assertFalse(timer.isRunning(), "Timer should not be running after stop().");
    }

    @Test
    @DisplayName("unPause() should set the timer back to a running state")
    void unPause() {
        timer.start();
        timer.stop(); // "Pausing" the timer
        assertFalse(timer.isRunning(), "Timer should be stopped before unPause().");

        timer.unPause();
        assertTrue(timer.isRunning(), "Timer should be running after unPause().");
    }

    @Test
    @DisplayName("addPenalty() should increase the total elapsed time")
    void addPenalty() {
        timer.start();
        try { Thread.sleep(SLEEP_DURATION); } catch (InterruptedException e) { fail("Test interrupted"); }
        
        timer.addPenalty(50); // Adds 50ms to the elapsed time by shifting start time
        timer.stop();

        long elapsed = timer.elapsedTime();
        assertTrue(elapsed >= SLEEP_DURATION + 50, "Elapsed time should be at least the sleep duration plus the penalty.");
        assertTrue(elapsed < SLEEP_DURATION + 50 + TOLERANCE, "Elapsed time should be reasonably close to the expected value.");
    }

    @Test
    @DisplayName("elapsedTime() should calculate the correct duration between start() and stop()")
    void elapsedTime() {
        timer.start();
        try { Thread.sleep(SLEEP_DURATION); } catch (InterruptedException e) { fail("Test interrupted"); }
        timer.stop();

        long elapsed = timer.elapsedTime();
        assertTrue(elapsed >= SLEEP_DURATION, "Elapsed time should be at least the sleep duration.");
        assertTrue(elapsed < SLEEP_DURATION + TOLERANCE, "Elapsed time should be reasonably close to the sleep duration.");
    }

    @Test
    @DisplayName("elapsedTime() returns negative value when timer is running")
    void elapsedTime_returnsNegativeWhenRunning() {
        timer.start();
        try { Thread.sleep(SLEEP_DURATION); } catch (InterruptedException e) { fail("Test interrupted"); }

        // Since stop() hasn't been called, stopTime is 0.
        // The result will be 0 - startTime, which is a large negative number.
        long elapsed = timer.elapsedTime();
        assertTrue(elapsed < 0, "Calling elapsedTime() on a running timer should not return a negative value, but it does.");
    }

    @Test
    @DisplayName("unPause() includes paused duration in total elapsed time")
    void unPause_includesPausedDuration() {
        timer.start(); // t=0
        try { Thread.sleep(SLEEP_DURATION); } catch (InterruptedException e) { fail("Test interrupted"); }
        
        timer.stop(); // "Pause" at t=100ms. Elapsed is ~100ms.
        long elapsedAfterPause = timer.elapsedTime();
        assertTrue(elapsedAfterPause >= SLEEP_DURATION && elapsedAfterPause < SLEEP_DURATION + TOLERANCE);

        try { Thread.sleep(SLEEP_DURATION); } catch (InterruptedException e) { fail("Test interrupted"); } // "Paused" for 100ms

        timer.unPause(); // "Resume" at t=200ms.
        try { Thread.sleep(SLEEP_DURATION); } catch (InterruptedException e) { fail("Test interrupted"); }

        timer.stop(); // Final stop at t=300ms.

        // Expected elapsed time = (first run) + (second run) = 100ms + 100ms = 200ms.
        // Actual elapsed time = (final stop time) - (original start time) = 300ms - 0ms = 300ms.
        // The 100ms pause was incorrectly included.
        long finalElapsed = timer.elapsedTime();
        long expectedTotalElapsed = SLEEP_DURATION * 2;

        assertTrue(expectedTotalElapsed + TOLERANCE >= finalElapsed, "Timer should not include pause time in total");
    }

    @Test
    @DisplayName("isRunning() should correctly reflect the timer's state through its lifecycle")
    void isRunning() {
        assertFalse(timer.isRunning(), "New timer should not be running.");
        
        timer.start();
        assertTrue(timer.isRunning(), "Timer should be running after start().");
        
        timer.stop();
        assertFalse(timer.isRunning(), "Timer should not be running after stop().");
        
        timer.unPause();
        assertTrue(timer.isRunning(), "Timer should be running after unPause().");

        timer.start(); // Restarting
        assertTrue(timer.isRunning(), "Timer should be running after being restarted.");
    }
}