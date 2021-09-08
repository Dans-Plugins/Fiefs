package dansplugins.fiefs.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Fief {
    private String name;
    private UUID ownerUUID;
    private String factionName;

    public Fief(String name, UUID ownerUUID, String factionName) {
        this.name = name;
        this.ownerUUID = ownerUUID;
        this.factionName = factionName;
    }

    public Fief(Map<String, String> fiefData) {
        this.load(fiefData);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public String getFactionName() {
        return factionName;
    }

    public void setFactionName(String factionName) {
        this.factionName = factionName;
    }

    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();;

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("name", gson.toJson(name));
        saveMap.put("ownerUUID", gson.toJson(ownerUUID));
        saveMap.put("factionName", gson.toJson(factionName));

        return saveMap;
    }

    private void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        name = gson.fromJson(data.get("name"), String.class);
        ownerUUID = UUID.fromString(gson.fromJson(data.get("ownerUUID"), String.class));
        factionName = gson.fromJson(data.get("factionName"), String.class);
    }
}
