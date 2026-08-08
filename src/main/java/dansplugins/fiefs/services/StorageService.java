package dansplugins.fiefs.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dansplugins.fiefs.Fiefs;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import dansplugins.fiefs.objects.ClaimedChunk;
import dansplugins.fiefs.objects.Fief;
import dansplugins.fiefs.utils.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel McCoy Stephenson
 */
public class StorageService {
    private final ConfigService configService;
    private final Fiefs fiefs;
    private final PersistentData persistentData;
    private final Logger logger;
    private final MedievalFactionsIntegrator medievalFactionsIntegrator;

    private final static String FILE_PATH = "./plugins/Fiefs/";
    private final static String FIEFS_FILE_NAME = "fiefs.json";
    private final static String CLAIMED_CHUNKS_FILE_NAME = "claimedChunks.json";
    private final static Type LIST_MAP_TYPE = new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();;

    // Set false whenever a load fails to fully parse, so save() doesn't overwrite
    // fiefs.json/claimedChunks.json with the empty or partial in-memory state (#153).
    private boolean loadCompletedCleanly = true;

    public StorageService(ConfigService configService, Fiefs fiefs, PersistentData persistentData, Logger logger, MedievalFactionsIntegrator medievalFactionsIntegrator) {
        this.configService = configService;
        this.fiefs = fiefs;
        this.persistentData = persistentData;
        this.logger = logger;
        this.medievalFactionsIntegrator = medievalFactionsIntegrator;
    }

    public void save() {
        if (!loadCompletedCleanly) {
            System.out.println("ERROR: skipping save because the last load did not complete cleanly. " +
                    "Fix " + FIEFS_FILE_NAME + "/" + CLAIMED_CHUNKS_FILE_NAME + " and restart to try again.");
            return;
        }
        saveFiefs();
        saveClaimedChunks();
        if (configService.hasBeenAltered()) {
            fiefs.saveConfig();
        }
    }

    public void load() {
        loadCompletedCleanly = true;
        loadFiefs();
        loadClaimedChunks();
    }

    /**
     * For tests: whether both load() calls this session parsed their files fully,
     * without needing to reach into the private flag directly.
     */
    boolean isLoadCompletedCleanly() {
        return loadCompletedCleanly;
    }

    private void saveFiefs() {
        // save each fief object individually
        List<Map<String, String>> fiefs = new ArrayList<>();
        for (Fief fief : persistentData.getFiefs()){
            fiefs.add(fief.save());
        }

        writeOutFiles(fiefs, FIEFS_FILE_NAME);
    }

    private void saveClaimedChunks() {
        // save each claimed chunk object individually
        List<Map<String, String>> claimedChunks = new ArrayList<>();
        for (ClaimedChunk claimedChunk : persistentData.getClaimedChunks()){
            claimedChunks.add(claimedChunk.save());
        }

        writeOutFiles(claimedChunks, CLAIMED_CHUNKS_FILE_NAME);
    }

    private void writeOutFiles(List<Map<String, String>> saveData, String fileName) {
        try {
            File parentFolder = new File(FILE_PATH);
            // mkdirs(), not mkdir(): mkdir() only creates the leaf, so it fails silently when
            // "./plugins/" itself is absent and the createNewFile() below then throws (#159).
            parentFolder.mkdirs();
            File file = new File(FILE_PATH + fileName);
            file.createNewFile();
            // try-with-resources, not a trailing close(): with close() as the last statement of the
            // try block it was skipped whenever the write threw, leaving the file both open and
            // unflushed while the catch below only logged (#164).
            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                outputStreamWriter.write(gson.toJson(saveData));
            }
        } catch(IOException e) {
            System.out.println("ERROR: " + e.toString());
        }
    }

    private void loadFiefs() {
        applyFiefs(FILE_PATH + FIEFS_FILE_NAME);
    }

    private void loadClaimedChunks() {
        applyClaimedChunks(FILE_PATH + CLAIMED_CHUNKS_FILE_NAME);
    }

    // Package-private so tests can exercise the parse-then-swap behavior directly.
    // Reading the file and constructing every entry happen inside the same try block, so a
    // malformed JSON file (not just a malformed entry, e.g. bad UUID) is caught the same way.
    void applyFiefs(String filename) {
        ArrayList<Fief> loaded = new ArrayList<>();
        try {
            ArrayList<HashMap<String, String>> data = loadDataFromFilename(filename);
            for (Map<String, String> fiefData : data) {
                loaded.add(new Fief(fiefData, medievalFactionsIntegrator, logger));
            }
        } catch (RuntimeException e) {
            // Parse only into a local list first, so a bad entry can't leave persistentData
            // partially cleared/repopulated (#153): either every entry loads, or none do.
            loadCompletedCleanly = false;
            System.out.println("ERROR: failed to load " + FIEFS_FILE_NAME + " cleanly, leaving existing " +
                    "in-memory fief data untouched: " + e);
            return;
        }

        persistentData.clearFiefs();
        for (Fief fief : loaded) {
            persistentData.addFief(fief);
        }
    }

    void applyClaimedChunks(String filename) {
        ArrayList<ClaimedChunk> loaded = new ArrayList<>();
        try {
            ArrayList<HashMap<String, String>> data = loadDataFromFilename(filename);
            for (Map<String, String> claimedChunkData : data) {
                loaded.add(new ClaimedChunk(claimedChunkData));
            }
        } catch (RuntimeException e) {
            loadCompletedCleanly = false;
            System.out.println("ERROR: failed to load " + CLAIMED_CHUNKS_FILE_NAME + " cleanly, leaving existing " +
                    "in-memory claimed chunk data untouched: " + e);
            return;
        }

        persistentData.clearClaimedChunks();
        for (ClaimedChunk claimedChunk : loaded) {
            persistentData.addChunk(claimedChunk);
        }
    }

    private ArrayList<HashMap<String, String>> loadDataFromFilename(String filename) {
        // FileNotFoundException covers more than "missing": per the FileInputStream(String)
        // contract it's also thrown when the path is a directory or otherwise can't be opened
        // for reading (e.g. no read permission). Checking existence first lets the catch below
        // tell "nothing to load" apart from "data is there but unreadable" (#162), instead of
        // treating both as an empty, cleanly-loaded file.
        if (!new File(filename).exists()) {
            return new ArrayList<>();
        }
        // try-with-resources on the stream itself as well as the reader wrapping it: the reader
        // was previously never closed at all, leaving the descriptor open until the garbage
        // collector got around to it, which on Windows keeps the file locked against the next
        // save (#164). Listing the FileInputStream separately also releases it if the reader
        // chain around it fails to construct.
        try (FileInputStream fileInputStream = new FileInputStream(filename);
             JsonReader reader = new JsonReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();;
            ArrayList<HashMap<String, String>> data = gson.fromJson(reader, LIST_MAP_TYPE);
            // Gson yields null for a zero-byte file, which a crash mid-save can leave behind
            // (FileOutputStream truncates before writing). An empty file is no data, not corrupt
            // data, so treat it like a missing file rather than failing the load and blocking
            // every subsequent save for the session (#160).
            if (data == null) {
                return new ArrayList<>();
            }
            return data;
        } catch (FileNotFoundException e) {
            // The file exists but couldn't be opened for reading. Surface this as an unclean
            // load (caught by applyFiefs()/applyClaimedChunks() as a RuntimeException) instead
            // of silently returning an empty list, so save() doesn't overwrite real on-disk data.
            throw new UncheckedIOException(e);
        } catch (IOException e) {
            // Only reachable from closing the reader, since every read failure surfaces from Gson
            // as a JsonIOException. Treated as an unclean load for the same reason as above: the
            // file could not be read end-to-end, so its contents must not be overwritten.
            throw new UncheckedIOException(e);
        }
    }
}