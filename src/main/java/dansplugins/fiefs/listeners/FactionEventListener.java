package dansplugins.fiefs.listeners;

import dansplugins.factionsystem.events.*;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.ClaimedChunk;
import dansplugins.fiefs.objects.Fief;
import dansplugins.fiefs.services.ChunkService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

/**
 * @author Daniel McCoy Stephenson
 */
public class FactionEventListener implements Listener {
    private final PersistentData persistentData;
    private final ChunkService chunkService;

    public FactionEventListener(PersistentData persistentData, ChunkService chunkService) {
        this.persistentData = persistentData;
        this.chunkService = chunkService;
    }

    @EventHandler()
    public void handle(FactionRenameEvent event) {
        String oldName = event.getCurrentName();
        String newName = event.getProposedName();
        for (Fief fief : persistentData.getFiefs()) {
            if (fief.getFactionName().equalsIgnoreCase(oldName)) {
                fief.setFactionName(newName);
            }
        }
    }

    @EventHandler()
    public void handle(FactionUnclaimEvent event) {
        ClaimedChunk claimedChunk = chunkService.getClaimedChunk(event.getChunk());
        if (claimedChunk != null) {
            persistentData.removeChunk(claimedChunk);
        }
    }

    @EventHandler()
    public void handle(FactionLeaveEvent event) {
        Fief fief = persistentData.getFief(event.getOfflinePlayer().getName());
        if (fief != null) {
            fief.removeMember(event.getOfflinePlayer().getUniqueId());

            // TODO: inform fief members that the player left the faction
        }
    }

    @EventHandler()
    public void handle(FactionDisbandEvent event) {
        ArrayList<Fief> toRemove = new ArrayList<>();
        for (Fief fief : persistentData.getFiefs()) {
            if (fief.getFactionName().equalsIgnoreCase(event.getFaction().getName())) {
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
        Fief fief = persistentData.getFief(event.getOfflinePlayer().getName());
        if (fief != null) {
            fief.removeMember(event.getOfflinePlayer().getUniqueId());
        }

        // TODO: inform fief members that the player was kicked from the faction
    }
}