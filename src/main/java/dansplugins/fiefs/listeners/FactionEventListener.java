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
    private final ChunkService chunkService;
    private final MedievalFactions medievalFactions;

    public FactionEventListener(PersistentData persistentData, ChunkService chunkService, MedievalFactions medievalFactions) {
        this.persistentData = persistentData;
        this.chunkService = chunkService;
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
        org.bukkit.Chunk bukkitChunk = world.getChunkAt(event.getClaim().getX(), event.getClaim().getZ());
        ClaimedChunk claimedChunk = chunkService.getClaimedChunk(bukkitChunk);
        if (claimedChunk != null) {
            persistentData.removeChunk(claimedChunk);
        }
    }

    @EventHandler()
    public void handle(FactionLeaveEvent event) {
        UUID playerUUID = UUID.fromString(event.getPlayerId().getValue());
        Fief fief = persistentData.getFief(playerUUID);
        if (fief != null) {
            fief.removeMember(playerUUID);

            // TODO: inform fief members that the player left the faction
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
        UUID playerUUID = UUID.fromString(event.getPlayerId().getValue());
        Fief fief = persistentData.getFief(playerUUID);
        if (fief != null) {
            fief.removeMember(playerUUID);
        }

        // TODO: inform fief members that the player was kicked from the faction
    }
}