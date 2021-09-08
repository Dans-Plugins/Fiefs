package dansplugins.fiefs.managers;

import dansplugins.factionsystem.objects.Faction;
import org.bukkit.Chunk;

public class ChunkManager {

    private static ChunkManager instance;

    private ChunkManager() {

    }

    public static ChunkManager getInstance() {
        if (instance == null) {
            instance = new ChunkManager();
        }
        return instance;
    }

    public Chunk getClaimedChunk(Chunk chunk) {
        // TODO: implement
        return null;
    }

    public boolean claimChunk(Chunk chunk, Faction faction) {
        // TODO: implement
        return false;
    }

    public boolean unclaimChunk(Chunk chunk, Faction faction) {
        // TODO: implement
        return false;
    }

}
