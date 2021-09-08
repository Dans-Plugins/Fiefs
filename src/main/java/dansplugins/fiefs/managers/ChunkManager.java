package dansplugins.fiefs.managers;

import dansplugins.fiefs.MedievalFactionsIntegrator;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.ClaimedChunk;
import dansplugins.fiefs.objects.Fief;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

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

    public ClaimedChunk getClaimedChunk(Chunk chunk) {
        for (ClaimedChunk claimedChunk : PersistentData.getInstance().getClaimedChunks()) {
            if (areChunksEqual(claimedChunk.getChunk(), chunk)) {
                return claimedChunk;
            }
        }
        return null;
    }

    public boolean claimChunk(Chunk chunk, Fief fief, Player player) {
        /*
        if (!MedievalFactionsIntegrator.getInstance().getAPI().isChunkClaimed(chunk)) {
            player.sendMessage(ChatColor.RED + "You can only claim land which belong to your faction.");
            return false;
        }
        */

        ClaimedChunk claimedChunk = getClaimedChunk(chunk);
        if (claimedChunk != null) {
            player.sendMessage(ChatColor.RED + "This chunk is already claimed by " + claimedChunk.getFief() + ".");
            return false;
        }

        ClaimedChunk newClaimedChunk = new ClaimedChunk(chunk, fief.getFactionName(), fief.getName());
        PersistentData.getInstance().addChunk(newClaimedChunk);
        player.sendMessage(ChatColor.GREEN + "Claimed.");
        return true;
    }

    public boolean unclaimChunk(Chunk chunk, Fief fief, Player player) {
        // check that chunk is actually claimed
        ClaimedChunk claimedChunk = getClaimedChunk(chunk);
        if (claimedChunk == null) {
            player.sendMessage(ChatColor.RED + "That chunk is not claimed by a fief.");
            return false;
        }

        // check that chunk is claimed by the player's fief
        if (!claimedChunk.getFief().equalsIgnoreCase(fief.getName())) {
            player.sendMessage(ChatColor.RED + "That chunk doesn't belong to your fief.");
            return false;
        }

        // unclaim the chunk
        PersistentData.getInstance().removeChunk(claimedChunk);
        return true;
    }

    private boolean areChunksEqual(Chunk chunk1, Chunk chunk2) {
        return (chunk1.getX() == chunk2.getX() && chunk1.getZ() == chunk2.getZ() && chunk1.getWorld().getName().equals(chunk2.getWorld().getName()));
    }

}
