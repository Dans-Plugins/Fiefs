package dansplugins.fiefs.eventhandlers;

import dansplugins.fiefs.objects.ClaimedChunk;
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
        // TODO: implement
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
