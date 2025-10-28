package com.s2tn.model;

/**
 * Simple timer class used to track elapsed time in milliseconds.
 * Supports start, stop, unpause, and adding penalties.
 */
public class Timer {
    private long startTime;
    private long stopTime;
    private boolean running;

    /** Creates a new timer initialized in a stopped state. */
    public Timer(){
        startTime = 0;
        stopTime = 0;
        running = false;
    }

    /** Starts or restarts the timer. */
    public void start(){
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }

    /** Stops the timer and records the stop time. */
    public void stop(){
        this.stopTime = System.currentTimeMillis();
        this.running = false;
    }

    /** Resumes the timer from a paused state. */
    public void unPause(){
        this.stopTime = 0;
        this.running = true;
    }

    /** Adds a time penalty by shifting the start time backward. */
    public void addPenalty(long penalty){
        this.startTime -= penalty;
    }

    /** Returns the elapsed time in milliseconds between start and stop. */
    public long elapsedTime(){
        return stopTime - startTime;
    }

    /** Returns true if the timer is currently running. */
    public boolean isRunning() {
        return running;
    }
}
