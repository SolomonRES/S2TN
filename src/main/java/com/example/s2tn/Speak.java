package com.example.s2tn;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public final class Speak {
    private static final String VOICE_NAME = "kevin16";
    private static volatile boolean enabled = true;  
    private static Voice voice;

    private Speak() {}

    public static void setEnabled(boolean on) {
        enabled = on;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void toggle() {
        enabled = !enabled;
    }

    public static synchronized void speak(String text) {
        if (!enabled || text == null || text.isBlank()) return;

        try {
            if (voice == null) {
                System.setProperty("freetts.voices",
                        "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
                VoiceManager vm = VoiceManager.getInstance();
                voice = vm.getVoice(VOICE_NAME);
                if (voice == null) {
                    System.err.println("Voice not found: " + VOICE_NAME);
                    return;
                }
                voice.allocate();
            }

            voice.speak(text);
        } catch (Throwable t) {
            System.err.println("TTS error: " + t.getMessage());
        }
    }

    public static synchronized void shutdown() {
        if (voice != null) {
            voice.deallocate();
            voice = null;
        }
    }
}
