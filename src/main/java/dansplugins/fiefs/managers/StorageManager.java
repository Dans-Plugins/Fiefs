package dansplugins.fiefs.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dansplugins.fiefs.Fiefs;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.ClaimedChunk;
import dansplugins.fiefs.objects.Fief;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageManager {

    private static StorageManager instance;

    private final static String FILE_PATH = "./plugins/Fiefs/";
    private final static String FIEFS_FILE_NAME = "fiefs.json";
    private final static String CLAIMED_CHUNKS_FILE_NAME = "claimedChunks.json";

    private final static Type LIST_MAP_TYPE = new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType();

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();;

    private StorageManager() {

    }

    public static StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    public void save() {
        saveFiefs();
        saveClaimedChunks();
        if (ConfigManager.getInstance().hasBeenAltered()) {
            Fiefs.getInstance().saveConfig();
        }
    }

    public void load() {
        loadFiefs();
        loadClaimedChunks();
    }

    private void saveFiefs() {
        // save each fief object individually
        List<Map<String, String>> fiefs = new ArrayList<>();
        for (Fief fief : PersistentData.getInstance().getFiefs()){
            fiefs.add(fief.save());
        }

        writeOutFiles(fiefs, FIEFS_FILE_NAME);
    }

    private void saveClaimedChunks() {
        // save each claimed chunk object individually
        List<Map<String, String>> claimedChunks = new ArrayList<>();
        for (ClaimedChunk claimedChunk : PersistentData.getInstance().getClaimedChunks()){
            claimedChunks.add(claimedChunk.save());
        }

        writeOutFiles(claimedChunks, CLAIMED_CHUNKS_FILE_NAME);
    }

    private void writeOutFiles(List<Map<String, String>> saveData, String fileName) {
        try {
            File parentFolder = new File(FILE_PATH);
            parentFolder.mkdir();
            File file = new File(FILE_PATH + fileName);
            file.createNewFile();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            outputStreamWriter.write(gson.toJson(saveData));
            outputStreamWriter.close();
        } catch(IOException e) {
            System.out.println("ERROR: " + e.toString());
        }
    }

    private void loadFiefs() {
        // load each fief
        PersistentData.getInstance().clearFiefs();

        ArrayList<HashMap<String, String>> data = loadDataFromFilename(FILE_PATH + FIEFS_FILE_NAME);

        for (Map<String, String> fiefData : data){
            Fief fief = new Fief(fiefData);
            PersistentData.getInstance().addFief(fief);
        }
    }

    private void loadClaimedChunks() {
        // load each claimed chunk
        PersistentData.getInstance().clearClaimedChunks();

        ArrayList<HashMap<String, String>> data = loadDataFromFilename(FILE_PATH + CLAIMED_CHUNKS_FILE_NAME);

        for (Map<String, String> claimedChunkData : data){
            ClaimedChunk claimedChunk = new ClaimedChunk(claimedChunkData);
            PersistentData.getInstance().addChunk(claimedChunk);
        }
    }

    private ArrayList<HashMap<String, String>> loadDataFromFilename(String filename) {
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();;
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
            return gson.fromJson(reader, LIST_MAP_TYPE);
        } catch (FileNotFoundException e) {
            // Fail silently because this can actually happen in normal use
        }
        return new ArrayList<>();
    }

}
