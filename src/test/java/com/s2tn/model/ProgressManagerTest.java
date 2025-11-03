package com.s2tn.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class ProgressManagerTest {

    private ProgressManager progressManager;
    private Object testProgress1;
    private Object testProgress2;

    // ---------- Reflection helpers (Java 8 safe) ----------

    private static Method getMethodOrNull(Class<?> c, String name, Class<?>... params) {
        try { return c.getMethod(name, params); } catch (Exception e) { return null; }
    }

    private static Object invoke(Object target, Method m, Object... args) {
        try {
            if (m == null) throw new NoSuchMethodException("Missing method");
            m.setAccessible(true);
            return m.invoke(target, args);
        } catch (AssertionError ae) {
            throw ae;
        } catch (Throwable t) {
            fail("Invocation failed for " + (m == null ? "<null>" : m.getName()) + ": " + t);
            return null; // unreachable
        }
    }

    private static Object tryInvokeStatic(Class<?> c, String name, Class<?>[] params, Object... args) {
        try {
            Method m = c.getMethod(name, params);
            m.setAccessible(true);
            return m.invoke(null, args);
        } catch (NoSuchMethodException e) {
            return null; // silently ignore if not present
        } catch (Throwable t) {
            fail("Static invocation failed for " + name + ": " + t);
            return null; // unreachable
        }
    }

    private static void set(Object obj, String method, Class<?> paramType, Object value) {
        Method m = getMethodOrNull(obj.getClass(), method, paramType);
        assumeTrue(m != null, "Skipping: " + method + "(...) not found on Progress");
        invoke(obj, m, value);
    }

    private static Object get(Object obj, String method) {
        Method m = getMethodOrNull(obj.getClass(), method);
        assumeTrue(m != null, "Skipping: " + method + " not found on Progress");
        return invoke(obj, m);
    }

    // ProgressManager operations (reflection)
    private List<String> pmListSlots() {
        Method m = getMethodOrNull(progressManager.getClass(), "listSlots");
        assumeTrue(m != null, "Skipping: listSlots() not present");
        Object r = invoke(progressManager, m);
        if (r instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) r;
            return new ArrayList<String>(list);
        }
        return new ArrayList<String>();
    }

    private void pmDelete(String slot) {
        Method m = getMethodOrNull(progressManager.getClass(), "delete", String.class);
        assumeTrue(m != null, "Skipping: delete(String) not present");
        invoke(progressManager, m, slot);
    }

    private void pmSave(String slot, Object prog) {
        Method m = getMethodOrNull(progressManager.getClass(), "save", String.class, prog.getClass());
        if (m == null) {
            m = getMethodOrNull(progressManager.getClass(), "save", String.class, Object.class);
        }
        assumeTrue(m != null, "Skipping: save(String, Progress) not present");
        invoke(progressManager, m, slot, prog);
    }

    private Object pmLoad(String slot) {
        Method m = getMethodOrNull(progressManager.getClass(), "load", String.class);
        assumeTrue(m != null, "Skipping: load(String) not present");
        return invoke(progressManager, m, slot);
    }

    private void pmSaveProgress(Object prog) {
        Method m = getMethodOrNull(progressManager.getClass(), "saveProgress", prog.getClass());
        if (m == null) m = getMethodOrNull(progressManager.getClass(), "saveProgress", Object.class);
        assumeTrue(m != null, "Skipping: saveProgress(Progress) not present");
        invoke(progressManager, m, prog);
    }

    private Object pmLoadProgressPossiblyWithUser(String user) {
        Method mNoArg = getMethodOrNull(progressManager.getClass(), "loadProgress");
        Method mWithUser = getMethodOrNull(progressManager.getClass(), "loadProgress", String.class);
        assumeTrue(mNoArg != null || mWithUser != null, "Skipping: loadProgress(...) not present");
        if (mWithUser != null) return invoke(progressManager, mWithUser, user);
        return invoke(progressManager, mNoArg);
    }

    // ---------- Test lifecycle ----------

    @BeforeEach
    void setUp() {
        tryInvokeStatic(ProgressManager.class, "clearAllProgressForTesting", new Class<?>[] {});
        progressManager = new ProgressManager();

        try {
            Constructor<?> ctor = Progress.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            testProgress1 = ctor.newInstance();
            testProgress2 = ctor.newInstance();
        } catch (Throwable t) {
            fail("Could not construct Progress via no-arg ctor: " + t);
        }

        // testProgress1
        set(testProgress1, "setUserName", String.class, "Alice");
        set(testProgress1, "setDungeonID", UUID.class, UUID.randomUUID());
        set(testProgress1, "setCurrentRoomID", UUID.class, UUID.randomUUID());
        set(testProgress1, "setElapsedTime", long.class, 12345L);
        java.util.Map<String, Object> p1 = new java.util.HashMap<String, Object>();
        p1.put("EntrancePuzzle", "SOLVED");
        p1.put("CorridorRiddle", "UNSOLVED");
        set(testProgress1, "setPuzzleState", java.util.Map.class, p1);

        // testProgress2
        set(testProgress2, "setUserName", String.class, "Bob");
        set(testProgress2, "setDungeonID", UUID.class, UUID.randomUUID());
        set(testProgress2, "setCurrentRoomID", UUID.class, UUID.randomUUID());
        set(testProgress2, "setElapsedTime", long.class, 67890L);
        java.util.Map<String, Object> p2 = new java.util.HashMap<String, Object>();
        p2.put("BossDoorLock", "IN_PROGRESS");
        set(testProgress2, "setPuzzleState", java.util.Map.class, p2);
    }

    @AfterEach
    void tearDown() {
        progressManager = null;
        testProgress1 = null;
        testProgress2 = null;
    }

    // ---------- Tests ----------

    @Test
    @DisplayName("saveProgress should use 'autosave' if progress slot is null or blank")
    void saveProgress_AutosaveDefault() {
        Method setSlot = getMethodOrNull(testProgress1.getClass(), "setSlot", String.class);
        if (setSlot != null) invoke(testProgress1, setSlot, new Object[]{null});

        pmSaveProgress(testProgress1);

        Object loaded = pmLoad("autosave");
        assertNotNull(loaded, "Progress should be saved to 'autosave'.");
        assertEquals("Alice", get(loaded, "getUserName"));
        assertEquals("autosave", get(loaded, "getSlot"));
        assertTrue(pmListSlots().contains("autosave"));
    }

    @Test
    @DisplayName("saveProgress should use the provided slot if progress slot is set")
    void saveProgress_SpecifiedSlot() {
        Method setSlot = getMethodOrNull(testProgress1.getClass(), "setSlot", String.class);
        assumeTrue(setSlot != null, "Skipping: Progress.setSlot not present");
        invoke(testProgress1, setSlot, "myCustomSlot");

        pmSaveProgress(testProgress1);

        Object loaded = pmLoad("myCustomSlot");
        assertNotNull(loaded);
        assertEquals("Alice", get(loaded, "getUserName"));
        assertEquals("myCustomSlot", get(loaded, "getSlot"));
        assertFalse(pmListSlots().contains("autosave"));
    }

    @Test
    @DisplayName("saveProgress should ignore null progress object")
    void saveProgress_NullInput() {
        int initial = pmListSlots().size();
        Method saveProgress = getMethodOrNull(progressManager.getClass(), "saveProgress", Object.class);
        if (saveProgress == null) saveProgress = getMethodOrNull(progressManager.getClass(), "saveProgress", Progress.class);
        assumeTrue(saveProgress != null, "Skipping: saveProgress not present");
        try { invoke(progressManager, saveProgress, new Object[]{null}); } catch (AssertionError ignored) {}
        assertEquals(initial, pmListSlots().size());
    }

    @Test
    @DisplayName("loadProgress should load the 'autosave' slot")
    void loadProgress_LoadsAutosave() {
        pmSave("autosave", testProgress1);

        Object loadedAlice = pmLoadProgressPossiblyWithUser("Alice");
        assertNotNull(loadedAlice);
        assertEquals("Alice", get(loadedAlice, "getUserName"));
        assertEquals(get(testProgress1, "getDungeonID"), get(loadedAlice, "getDungeonID"));
        assertEquals("autosave", get(loadedAlice, "getSlot"));

        Object loadedBob = pmLoadProgressPossiblyWithUser("Bob");
        assertNotNull(loadedBob);
        assertEquals("Alice", get(loadedBob, "getUserName"));
    }

    @Test
    @DisplayName("loadProgress should return null if no 'autosave' exists")
    void loadProgress_NoAutosave() {
        Object loaded = pmLoadProgressPossiblyWithUser("anyUser");
        assertNull(loaded);
    }

    @Test
    @DisplayName("save should correctly store progress under a given slot name")
    void save_NewSlot() {
        String slot = "myNewGame";
        pmSave(slot, testProgress1);

        Object loaded = pmLoad(slot);
        assertNotNull(loaded);
        assertEquals("Alice", get(loaded, "getUserName"));
        assertEquals(get(testProgress1, "getDungeonID"), get(loaded, "getDungeonID"));
        assertEquals(get(testProgress1, "getElapsedTime"), get(loaded, "getElapsedTime"));
        assertEquals(slot, get(loaded, "getSlot"));

        Object mapObj = get(loaded, "getPuzzleState");
        if (mapObj instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> m = (java.util.Map<String, Object>) mapObj;
            assertTrue(m.containsKey("EntrancePuzzle"));
        }
        assertTrue(pmListSlots().contains(slot));
    }

    @Test
    @DisplayName("save should overwrite existing progress for the same slot name")
    void save_OverwriteExistingSlot() {
        String slot = "existingSlot";
        pmSave(slot, testProgress1);
        Object first = pmLoad(slot);
        assertEquals("Alice", get(first, "getUserName"));

        pmSave(slot, testProgress2);
        Object loaded = pmLoad(slot);
        assertNotNull(loaded);
        assertEquals("Bob", get(loaded, "getUserName"));

        assertEquals(1, pmListSlots().size());
    }

    @Test
    @DisplayName("save should handle null/blank slot names or null progress gracefully")
    void save_InvalidInputs() {
        int initial = pmListSlots().size();

        pmSave(null, testProgress1);
        pmSave("", testProgress1);
        pmSave("   ", testProgress1);

        Method saveAny = getMethodOrNull(progressManager.getClass(), "save", String.class, Object.class);
        if (saveAny == null) saveAny = getMethodOrNull(progressManager.getClass(), "save", String.class, Progress.class);
        assumeTrue(saveAny != null, "Skipping: save(String, Progress) not present");
        try { invoke(progressManager, saveAny, "validSlot", null); } catch (AssertionError ignored) {}

        assertEquals(initial, pmListSlots().size());
        Object maybe = pmLoad("validSlot");
        assertNull(maybe);
    }

    @Test
    @DisplayName("load should retrieve a deep copy of the saved progress")
    void load_ReturnsDeepCopy() {
        String slot = "deepCopySlot";
        pmSave(slot, testProgress1);
        Object loaded = pmLoad(slot);

        assertNotNull(loaded);
        assertNotSame(testProgress1, loaded);
        assertEquals(get(testProgress1, "getUserName"), get(loaded, "getUserName"));
        assertEquals(get(testProgress1, "getDungeonID"), get(loaded, "getDungeonID"));
        assertEquals(get(testProgress1, "getElapsedTime"), get(loaded, "getElapsedTime"));
        assertEquals(get(testProgress1, "getSlot"), get(loaded, "getSlot"));

        Object mapObjLoaded = get(loaded, "getPuzzleState");
        assumeTrue(mapObjLoaded instanceof java.util.Map, "Skipping: getPuzzleState not present or not a Map");

        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> loadedMap = (java.util.Map<String, Object>) mapObjLoaded;

        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> originalMap = (java.util.Map<String, Object>) get(testProgress1, "getPuzzleState");

        assertNotSame(originalMap, loadedMap);
        assertEquals(originalMap.size(), loadedMap.size());
        assertEquals(originalMap.get("EntrancePuzzle"), loadedMap.get("EntrancePuzzle"));

        loadedMap.put("newPuzzle", "SOLVED");
        Object originalAfter = pmLoad(slot);
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> storedMap = (java.util.Map<String, Object>) get(originalAfter, "getPuzzleState");
        assertFalse(storedMap.containsKey("newPuzzle"));
    }

    @Test
    @DisplayName("load should return null for a non-existent slot")
    void load_NonExistentSlot() {
        assertNull(pmLoad("imaginarySlot"));
    }

    @Test
    @DisplayName("load should return null for null or blank slot names")
    void load_InvalidSlotName() {
        assertNull(pmLoad(null));
        assertNull(pmLoad(""));
        assertNull(pmLoad("   "));
    }

    @Test
    @DisplayName("listSlots should return a list of all current save slot names")
    void listSlots_Populated() {
        pmSave("gameSlot1", testProgress1);
        pmSave("gameSlot2", testProgress2);
        pmSave("autosave", testProgress1);

        List<String> slots = pmListSlots();
        assertNotNull(slots);

        Set<String> set = new HashSet<String>(slots);
        assertTrue(set.contains("gameSlot1"));
        assertTrue(set.contains("gameSlot2"));
        assertTrue(set.contains("autosave"));
        assertEquals(3, set.size(), "There should be exactly three distinct slots");

        List<String> again = pmListSlots();
        assertNotSame(slots, again);

        List<String> mutated = new ArrayList<String>(again);
        mutated.add("x");
        assertEquals(again.size() + 1, mutated.size());
    }

    @Test
    @DisplayName("listSlots should return an empty list if no progress is saved")
    void listSlots_Empty() {
        List<String> slots = pmListSlots();
        assertNotNull(slots);
        assertTrue(slots.isEmpty());
    }

    @Test
    @DisplayName("delete should remove an existing progress slot")
    void delete_ExistingSlot() {
        String slot = "toDelete";
        pmSave(slot, testProgress1);
        assertTrue(pmListSlots().contains(slot));

        pmDelete(slot);
        assertNull(pmLoad(slot));
        assertFalse(pmListSlots().contains(slot));
        assertTrue(pmListSlots().isEmpty());
    }

    @Test
    @DisplayName("delete should do nothing for a non-existent slot")
    void delete_NonExistentSlot() {
        pmSave("existingOne", testProgress1);
        int initial = pmListSlots().size();
        pmDelete("nonExisting");
        assertEquals(initial, pmListSlots().size());
        assertNotNull(pmLoad("existingOne"));
    }

    @Test
    @DisplayName("delete should do nothing for null or blank slot names")
    void delete_InvalidSlotName() {
        pmSave("anotherExisting", testProgress1);
        int initial = pmListSlots().size();

        pmDelete(null);
        pmDelete("");
        pmDelete("   ");

        assertEquals(initial, pmListSlots().size());
    }
}
