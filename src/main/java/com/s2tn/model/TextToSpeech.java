package com.s2tn.model;

/**
 * Provides text-to-speech functionality for the application.
 */
public interface TextToSpeech {

    /**
     * Converts the specified text into speech.
     *
     * @param text the text to be spoken aloud
     */
    void speak(String text);
}
