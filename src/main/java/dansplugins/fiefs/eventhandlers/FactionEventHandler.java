package dansplugins.fiefs.eventhandlers;

import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import dansplugins.factionsystem.events.FactionDisbandEvent;
import dansplugins.factionsystem.events.FactionKickEvent;
import dansplugins.factionsystem.events.FactionLeaveEvent;
import dansplugins.factionsystem.events.FactionRenameEvent;
import dansplugins.factionsystem.events.FactionUnclaimEvent;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.ClaimedChunk;
import dansplugins.fiefs.objects.Fief;
import dansplugins.fiefs.services.LocalChunkService;

/**
 * @author Daniel McCoy Stephenson
 */
public class FactionEventHandler implements Listener {

    @EventHandler()
    public void handle(FactionRenameEvent event) {
        String oldName = event.getCurrentName();
        String newName = event.getProposedName();
        for (Fief fief : PersistentData.getInstance().getFiefs()) {
            if (fief.getFactionName().equalsIgnoreCase(oldName)) {
                fief.setFactionName(newName);
            }
        }
    }

    @EventHandler()
    public void handle(FactionUnclaimEvent event) {
        ClaimedChunk claimedChunk = LocalChunkService.getInstance().getClaimedChunk(event.getChunk());
        if (claimedChunk != null) {
            PersistentData.getInstance().removeChunk(claimedChunk);
        }
    }

    @EventHandler()
    public void handle(FactionLeaveEvent event) {
        Fief fief = PersistentData.getInstance().getFief(event.getOfflinePlayer().getName());
        if (fief != null) {
            fief.removeMember(event.getOfflinePlayer().getUniqueId());

            // TODO: inform fief members that the player left the faction
        }
    }

    @EventHandler()
    public void handle(FactionDisbandEvent event) {
        ArrayList<Fief> toRemove = new ArrayList<>();
        for (Fief fief : PersistentData.getInstance().getFiefs()) {
            if (fief.getFactionName().equalsIgnoreCase(event.getFaction().getName())) {
                toRemove.add(fief);
            }
        }
        for (Fief fief : toRemove) {
            // TODO: inform fief members that the faction has been disbanded

            PersistentData.getInstance().removeFief(fief);
        }
    }

    @EventHandler()
    public void handle(FactionKickEvent event) {
        Fief fief = PersistentData.getInstance().getFief(event.getOfflinePlayer().getName());
        if (fief != null) {
            fief.removeMember(event.getOfflinePlayer().getUniqueId());
        }

        // TODO: inform fief members that the player was kicked from the faction
    }
}