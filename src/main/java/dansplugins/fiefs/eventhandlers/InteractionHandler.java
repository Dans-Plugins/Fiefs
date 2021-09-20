package dansplugins.fiefs.eventhandlers;

import dansplugins.fiefs.Fiefs;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.managers.ChunkManager;
import dansplugins.fiefs.objects.ClaimedChunk;
import dansplugins.fiefs.objects.Fief;
import dansplugins.fiefs.utils.Logger;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
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

        Fief playersFief = PersistentData.getInstance().getFief(player);
        if (playersFief == null) {
            return;
        }

        if (shouldEventBeCancelled(claimedChunk, player)) {
            if (Fiefs.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Cancelling Block Break event."); }
            event.setCancelled(true);
        }
    }

    @EventHandler()
    public void handle(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        Block clickedBlock = event.getBlock();
        ClaimedChunk claimedChunk = ChunkManager.getInstance().getClaimedChunk(clickedBlock.getChunk());
        if (claimedChunk == null) {
            return;
        }

        Fief playersFief = PersistentData.getInstance().getFief(player);
        if (playersFief == null) {
            return;
        }

        if (shouldEventBeCancelled(claimedChunk, player)) {
            if (Fiefs.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Cancelling Block Place event."); }
            event.setCancelled(true);
        }
    }

    @EventHandler()
    public void handle(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) {
            return;
        }

        ClaimedChunk claimedChunk = ChunkManager.getInstance().getClaimedChunk(clickedBlock.getChunk());
        if (claimedChunk == null) {
            return;
        }

        if (shouldEventBeCancelled(claimedChunk, player)) {
            if (Fiefs.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Cancelling Player Interact event."); }
            event.setCancelled(true);
        }
    }

    @EventHandler()
    public void handle(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();

        Location location = null;

        if (clickedEntity instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand) clickedEntity;

            // get chunk that armor stand is in
            location = armorStand.getLocation();
        }
        else if (clickedEntity instanceof ItemFrame) {
            if (Fiefs.getInstance().isDebugEnabled()) {
                System.out.println("DEBUG: ItemFrame interaction captured in PlayerInteractAtEntityEvent!");
            }
            ItemFrame itemFrame = (ItemFrame) clickedEntity;

            // get chunk that armor stand is in
            location = itemFrame.getLocation();
        }

        if (location != null) {
            Chunk chunk = location.getChunk();
            ClaimedChunk claimedChunk = ChunkManager.getInstance().getClaimedChunk(chunk);

            if (shouldEventBeCancelled(claimedChunk, player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler()
    public void handle(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getRemover();

        Entity entity = event.getEntity();

        // get chunk that entity is in
        ClaimedChunk claimedChunk = ChunkManager.getInstance().getClaimedChunk(entity.getLocation().getChunk());

        if (shouldEventBeCancelled(claimedChunk, player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler()
    public void handle(PlayerBucketFillEvent event) {
        if (Fiefs.getInstance().isDebugEnabled()) { System.out.println("DEBUG: A player is attempting to fill a bucket!"); }

        Player player = event.getPlayer();

        Block clickedBlock = event.getBlockClicked();

        ClaimedChunk claimedChunk = ChunkManager.getInstance().getClaimedChunk(clickedBlock.getChunk());

        if (shouldEventBeCancelled(claimedChunk, player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler()
    public void handle(PlayerBucketEmptyEvent event) {
        if (Fiefs.getInstance().isDebugEnabled()) { System.out.println("DEBUG: A player is attempting to empty a bucket!"); }

        Player player = event.getPlayer();

        Block clickedBlock = event.getBlockClicked();

        ClaimedChunk claimedChunk = ChunkManager.getInstance().getClaimedChunk(clickedBlock.getChunk());

        if (shouldEventBeCancelled(claimedChunk, player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler()
    public void handle(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();

        if (clickedEntity instanceof ItemFrame) {
            if (Fiefs.getInstance().isDebugEnabled()) {
                Logger.getInstance().log("ItemFrame interaction captured in PlayerInteractEntityEvent!");
            }
            ItemFrame itemFrame = (ItemFrame) clickedEntity;

            // get chunk that armor stand is in
            Location location = itemFrame.getLocation();
            Chunk chunk = location.getChunk();
            ClaimedChunk claimedChunk = ChunkManager.getInstance().getClaimedChunk(chunk);

            if (shouldEventBeCancelled(claimedChunk, player)) {
                event.setCancelled(true);
            }
        }
    }

    private boolean shouldEventBeCancelled(ClaimedChunk claimedChunk, Player player) {
        if (claimedChunk == null) {
            if (Fiefs.getInstance().isDebugEnabled()) { System.out.println("[Fiefs] Claimed chunk was null."); }
            return false;
        }
        Fief chunkHolder = PersistentData.getInstance().getFief(claimedChunk.getFief());
        Fief playersFief = PersistentData.getInstance().getFief(player);

        boolean claimedLandProtected = (boolean) playersFief.getFlags().getFlag("claimedLandProtected");

        if (!claimedLandProtected) {
            return false;
        }

        return !chunkHolder.equals(playersFief);
    }

}
