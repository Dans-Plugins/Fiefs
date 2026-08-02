package dansplugins.fiefs.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.Fief;
import dansplugins.fiefs.utils.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Characterization tests for the Dans-Plugins/Fiefs#153 fix: a load that fails to parse
 * fully must never clear/partially repopulate {@link PersistentData}, and must block
 * {@link StorageService#save()} from truncating the on-disk files with an incomplete
 * in-memory state. The Medieval Factions integrator and Bukkit types are not exercised
 * for the fiefs path since {@link Fief}'s map constructor doesn't call into them.
 */
class StorageServiceTest {

    private static final Logger NULL_LOGGER = new Logger(null);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // Bukkit always creates the top-level "plugins/" folder before onEnable() runs (it's
    // where the server loads plugin jars from), so StorageService's single-level mkdir() of
    // "plugins/Fiefs/" only ever needs to create the leaf. Recreate that guarantee here.
    private static final Path PLUGINS_TOP_LEVEL_DIR = Path.of("./plugins/");
    private static final Path PLUGINS_DIR = Path.of("./plugins/Fiefs/");

    private Path tempFile;

    @AfterEach
    void cleanup() throws IOException {
        if (tempFile != null) {
            Files.deleteIfExists(tempFile);
        }
        deleteRecursively(PLUGINS_TOP_LEVEL_DIR);
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException ignored) {
                    // best-effort test cleanup
                }
            });
        }
    }

    private StorageService newStorageService(PersistentData persistentData) {
        return new StorageService(new ConfigService(null), null, persistentData, NULL_LOGGER, null);
    }

    private Fief newFief(String name) {
        return new Fief(null, name, UUID.randomUUID(), "faction-1", NULL_LOGGER);
    }

    private Path writeJson(Object data) throws IOException {
        tempFile = Files.createTempFile("fiefs-storage-test", ".json");
        Files.write(tempFile, GSON.toJson(data).getBytes(StandardCharsets.UTF_8));
        return tempFile;
    }

    @Test
    void applyFiefs_populatesPersistentDataFromValidFile() throws IOException {
        PersistentData persistentData = new PersistentData(null);
        StorageService storageService = newStorageService(persistentData);
        List<Map<String, String>> data = new ArrayList<>();
        data.add(newFief("Testopia").save());
        Path file = writeJson(data);

        storageService.applyFiefs(file.toString());

        assertEquals(1, persistentData.getFiefs().size());
        assertEquals("Testopia", persistentData.getFiefs().get(0).getName());
        assertTrue(storageService.isLoadCompletedCleanly());
    }

    @Test
    void applyFiefs_missingFileLeavesPersistentDataEmptyButCountsAsClean() {
        PersistentData persistentData = new PersistentData(null);
        StorageService storageService = newStorageService(persistentData);

        storageService.applyFiefs("./does-not-exist-" + UUID.randomUUID() + ".json");

        assertEquals(0, persistentData.getFiefs().size());
        assertTrue(storageService.isLoadCompletedCleanly());
    }

    /**
     * Regression guard for #153: previously, {@code loadFiefs()} cleared persistentData
     * before parsing, so a bad entry (e.g. the unguarded {@code UUID.fromString(...)} in
     * {@code Fief.load()}) left the in-memory state permanently empty instead of intact.
     */
    @Test
    void applyFiefs_malformedEntryDoesNotClearOrPartiallyPopulatePersistentData() throws IOException {
        PersistentData persistentData = new PersistentData(null);
        Fief existing = newFief("Preexisting");
        persistentData.addFief(existing);
        StorageService storageService = newStorageService(persistentData);

        List<Map<String, String>> data = new ArrayList<>();
        data.add(newFief("Good").save());
        Map<String, String> corrupted = newFief("Corrupted").save();
        corrupted.put("ownerUUID", GSON.toJson("not-a-uuid"));
        data.add(corrupted);
        Path file = writeJson(data);

        storageService.applyFiefs(file.toString());

        // Both the pre-existing fief and the well-formed "Good" entry that came before the bad
        // one in the file must survive untouched: the load is all-or-nothing, not partial.
        assertEquals(1, persistentData.getFiefs().size());
        assertEquals(existing, persistentData.getFiefs().get(0));
        assertFalse(storageService.isLoadCompletedCleanly());
    }

    @Test
    void applyFiefs_malformedJsonDoesNotClearPersistentData() throws IOException {
        PersistentData persistentData = new PersistentData(null);
        Fief existing = newFief("Preexisting");
        persistentData.addFief(existing);
        StorageService storageService = newStorageService(persistentData);
        tempFile = Files.createTempFile("fiefs-storage-test", ".json");
        Files.write(tempFile, "{ not valid json [".getBytes(StandardCharsets.UTF_8));

        storageService.applyFiefs(tempFile.toString());

        assertEquals(1, persistentData.getFiefs().size());
        assertFalse(storageService.isLoadCompletedCleanly());
    }

    /**
     * {@link dansplugins.fiefs.objects.ClaimedChunk}'s map constructor dereferences Bukkit's
     * server, which is unavailable outside a running plugin and so always throws here. That
     * exercises the same all-or-nothing guard a real malformed claimed-chunk entry would hit
     * at runtime; the happy path is Bukkit-coupled and is instead covered by manual server
     * validation (see PR description).
     */
    @Test
    void applyClaimedChunks_entryConstructionFailureDoesNotClearPersistentData() throws IOException {
        PersistentData persistentData = new PersistentData(null);
        StorageService storageService = newStorageService(persistentData);
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> chunkData = newFief("Testopia").save(); // any map shape triggers ClaimedChunk.load()
        data.add(chunkData);
        Path file = writeJson(data);

        storageService.applyClaimedChunks(file.toString());

        assertEquals(0, persistentData.getNumChunks());
        assertFalse(storageService.isLoadCompletedCleanly());
    }

    @Test
    void applyClaimedChunks_emptyFileLeavesPersistentDataEmptyAndClean() {
        PersistentData persistentData = new PersistentData(null);
        StorageService storageService = newStorageService(persistentData);

        storageService.applyClaimedChunks("./does-not-exist-" + UUID.randomUUID() + ".json");

        assertEquals(0, persistentData.getNumChunks());
        assertTrue(storageService.isLoadCompletedCleanly());
    }

    /**
     * Regression guard for #153: previously {@code save()} unconditionally serialized
     * whatever was in memory, so a failed load got written straight over fiefs.json.
     */
    @Test
    void save_skipsWritingWhenLoadDidNotCompleteCleanly() throws IOException {
        assumeFalse(Files.exists(PLUGINS_DIR), "test expects no pre-existing ./plugins/Fiefs/ directory");

        PersistentData persistentData = new PersistentData(null);
        StorageService storageService = newStorageService(persistentData);
        Map<String, String> corrupted = newFief("Corrupted").save();
        corrupted.put("ownerUUID", GSON.toJson("not-a-uuid"));
        List<Map<String, String>> data = new ArrayList<>();
        data.add(corrupted);
        Path file = writeJson(data);
        storageService.applyFiefs(file.toString());
        assertFalse(storageService.isLoadCompletedCleanly());

        assertDoesNotThrow(storageService::save);

        assertFalse(Files.exists(PLUGINS_DIR), "save() must not write ./plugins/Fiefs/ after an incomplete load");
    }

    @Test
    void save_writesNormallyWhenLoadCompletedCleanly() throws IOException {
        assumeFalse(Files.exists(PLUGINS_DIR), "test expects no pre-existing ./plugins/Fiefs/ directory");
        Files.createDirectories(PLUGINS_TOP_LEVEL_DIR);

        PersistentData persistentData = new PersistentData(null);
        persistentData.addFief(newFief("Testopia"));
        StorageService storageService = newStorageService(persistentData);
        assertTrue(storageService.isLoadCompletedCleanly());

        storageService.save();

        assertTrue(Files.exists(PLUGINS_DIR.resolve("fiefs.json")));
    }
}
