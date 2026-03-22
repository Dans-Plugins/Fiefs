package dansplugins.fiefs.listeners;

import com.dansplugins.factionsystem.MedievalFactions;
import com.dansplugins.factionsystem.event.faction.*;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.ClaimedChunk;
import dansplugins.fiefs.objects.Fief;
import dansplugins.fiefs.services.ChunkService;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public class FactionEventListener implements Listener {
    private final PersistentData persistentData;
    private final MedievalFactions medievalFactions;

    public FactionEventListener(PersistentData persistentData, MedievalFactions medievalFactions) {
        this.persistentData = persistentData;
        this.medievalFactions = medievalFactions;
    }

    @EventHandler()
    public void handle(FactionRenameEvent event) {
        com.dansplugins.factionsystem.faction.MfFaction faction = 
            medievalFactions.getServices().getFactionService().getFaction(event.getFactionId());
        if (faction == null) {
            return;
        }
        String newName = event.getName();
        
        // Update all fiefs where any member belongs to this faction
        for (Fief fief : persistentData.getFiefs()) {
            // Check if any fief member is in the renamed faction
            boolean belongsToFaction = false;
            for (UUID memberUUID : fief.getMembers()) {
                com.dansplugins.factionsystem.player.MfPlayerId playerId = 
                    new com.dansplugins.factionsystem.player.MfPlayerId(memberUUID.toString());
                com.dansplugins.factionsystem.faction.MfFaction memberFaction = 
                    medievalFactions.getServices().getFactionService().getFaction(playerId);
                if (memberFaction != null && memberFaction.getId().equals(event.getFactionId())) {
                    belongsToFaction = true;
                    break;
                }
            }
            if (belongsToFaction) {
                fief.setFactionName(newName);
            }
        }
    }

    @EventHandler()
    public void handle(FactionUnclaimEvent event) {
        World world = Bukkit.getWorld(event.getClaim().getWorldId());
        if (world == null) {
            // World not loaded or unknown, cannot process unclaim
            return;
        }
        
        String worldName = world.getName();
        int chunkX = event.getClaim().getX();
        int chunkZ = event.getClaim().getZ();
        
        // Remove claim by matching world name and coordinates without loading the chunk
        ClaimedChunk toRemove = null;
        for (ClaimedChunk claimedChunk : persistentData.getClaimedChunks()) {
            String claimWorld = claimedChunk.getWorld();
            if (claimWorld != null && claimWorld.equals(worldName)) {
                // Guard against stale ClaimedChunk entries where the underlying chunk is null
                if (claimedChunk.getChunk() == null) {
                    continue;
                }
                double[] coords = claimedChunk.getCoordinates();
                int claimX = (int)coords[0];
                int claimZ = (int)coords[1];
                if (claimX == chunkX && claimZ == chunkZ) {
                    toRemove = claimedChunk;
                    break;
                }
            }
        }
        
        if (toRemove != null) {
            persistentData.removeChunk(toRemove);
        }
    }

    @EventHandler()
    public void handle(FactionLeaveEvent event) {
        try {
            UUID playerUUID = UUID.fromString(event.getPlayerId().getValue());
            Fief fief = persistentData.getFief(playerUUID);
            if (fief != null) {
                fief.removeMember(playerUUID);

                // TODO: inform fief members that the player left the faction
            }
        } catch (IllegalArgumentException e) {
            medievalFactions.getLogger().warning("Invalid player UUID format in FactionLeaveEvent: " + event.getPlayerId().getValue());
        }
    }

    @EventHandler()
    public void handle(FactionDisbandEvent event) {
        com.dansplugins.factionsystem.faction.MfFaction faction = 
            medievalFactions.getServices().getFactionService().getFaction(event.getFactionId());
        if (faction == null) {
            return;
        }
        ArrayList<Fief> toRemove = new ArrayList<>();
        for (Fief fief : persistentData.getFiefs()) {
            if (fief.getFactionName().equalsIgnoreCase(faction.getName())) {
                toRemove.add(fief);
            }
        }
        for (Fief fief : toRemove) {
            // TODO: inform fief members that the faction has been disbanded

            persistentData.removeFief(fief);
        }
    }

    @EventHandler()
    public void handle(FactionKickEvent event) {
        try {
            UUID playerUUID = UUID.fromString(event.getPlayerId().getValue());
            Fief fief = persistentData.getFief(playerUUID);
            if (fief != null) {
                fief.removeMember(playerUUID);
            }

            // TODO: inform fief members that the player was kicked from the faction
        } catch (IllegalArgumentException e) {
            medievalFactions.getLogger().warning("Invalid player UUID format in FactionKickEvent: " + event.getPlayerId().getValue());
        }
    }
}