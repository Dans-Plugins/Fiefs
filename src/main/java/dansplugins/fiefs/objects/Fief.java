package dansplugins.fiefs.objects;

import java.util.ArrayList;
import java.util.UUID;

public class Fief {
    private String name;
    private UUID ownerUUID;
    private String factionName;

    private ArrayList<ClaimedChunk> claimedChunks = new ArrayList<>();

    public Fief(String name, UUID ownerUUID, String factionName) {
        this.name = name;
        this.ownerUUID = ownerUUID;
        this.factionName = factionName;
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

    public void addChunk(ClaimedChunk chunk) {
        claimedChunks.add(chunk);
    }

    public void removeChunk(ClaimedChunk chunk) {
        claimedChunks.remove(chunk);
    }

    public int getNumChunks() {
        return claimedChunks.size();
    }
}
