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

    public StorageService(ConfigService configService, Fiefs fiefs, PersistentData persistentData, Logger logger, MedievalFactionsIntegrator medievalFactionsIntegrator) {
        this.configService = configService;
        this.fiefs = fiefs;
        this.persistentData = persistentData;
        this.logger = logger;
        this.medievalFactionsIntegrator = medievalFactionsIntegrator;
    }

    public void save() {
        saveFiefs();
        saveClaimedChunks();
        if (configService.hasBeenAltered()) {
            fiefs.saveConfig();
        }
    }

    public void load() {
        loadFiefs();
        loadClaimedChunks();
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
        persistentData.clearFiefs();

        ArrayList<HashMap<String, String>> data = loadDataFromFilename(FILE_PATH + FIEFS_FILE_NAME);

        for (Map<String, String> fiefData : data){
            Fief fief = new Fief(fiefData, medievalFactionsIntegrator, logger);
            persistentData.addFief(fief);
        }
    }

    private void loadClaimedChunks() {
        persistentData.clearClaimedChunks();

        ArrayList<HashMap<String, String>> data = loadDataFromFilename(FILE_PATH + CLAIMED_CHUNKS_FILE_NAME);

        for (Map<String, String> claimedChunkData : data){
            ClaimedChunk claimedChunk = new ClaimedChunk(claimedChunkData);
            persistentData.addChunk(claimedChunk);
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