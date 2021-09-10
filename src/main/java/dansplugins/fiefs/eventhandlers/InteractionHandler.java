package dansplugins.fiefs.eventhandlers;

import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.fiefs.Fiefs;
import dansplugins.fiefs.MedievalFactionsIntegrator;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.managers.ChunkManager;
import dansplugins.fiefs.objects.ClaimedChunk;
import dansplugins.fiefs.objects.Fief;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;

public class InteractionHandler implements Listener {

    @EventHandler()
    public void handle(BlockBreakEvent event) {
        Player player = event.getPlayer();

        Block brokenBlock = event.getBlock();
        ClaimedChunk claimedChunk = ChunkManager.getInstance().getClaimedChunk(brokenBlock.getChunk());
        if (claimedChunk == null) {
            return;
        }

        MF_Faction playersFaction = MedievalFactionsIntegrator.getInstance().getAPI().getFaction(player);
        if (playersFaction == null) {
            return;
        }

        if (!playersFaction.getName().equalsIgnoreCase(claimedChunk.getFaction())) {
            return;
        }

        Fief playersFief = PersistentData.getInstance().getFief(player);
        if (playersFief == null) {
            return;
        }

        if (!claimedChunk.getFief().equalsIgnoreCase(playersFief.getName())) {
            if (Fiefs.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Cancelling Block Break event."); }
            event.setCancelled(true);
        }
    }

    @EventHandler()
    public void handle(BlockPlaceEvent event) {
        // TODO: implement
    }

    @EventHandler()
    public void handle(PlayerInteractEvent event) {
        // TODO: implement
    }

    @EventHandler()
    public void handle(PlayerInteractAtEntityEvent event) {
        // TODO: implement
    }

    @EventHandler()
    public void handle(HangingBreakByEntityEvent event) {
        // TODO: implement
    }

    @EventHandler()
    public void handle(PlayerBucketFillEvent event) {
        // TODO: implement
    }

    @EventHandler()
    public void handle(PlayerBucketEmptyEvent event) {
        // TODO: implement
    }

    @EventHandler()
    public void handle(PlayerInteractEntityEvent event) {
        // TODO: implement
    }

    private boolean shouldEventBeCancelled(ClaimedChunk claimedChunk, Player player) {
        // TODO: implement
        return false;
    }

}
