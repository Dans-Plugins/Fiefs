package dansplugins.fiefs.eventhandlers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import dansplugins.fiefs.objects.ClaimedChunk;
import dansplugins.fiefs.services.LocalChunkService;
import dansplugins.fiefs.services.LocalConfigService;

/**
 * @author Daniel McCoy Stephenson
 */
public class MoveHandler implements Listener {

    @EventHandler()
    public void handle(PlayerMoveEvent event) {

        if (!LocalConfigService.getInstance().getBoolean("enableTerritoryAlerts")) {
            // territory alerts are disabled
            return;
        }

        Player player = event.getPlayer();

        ClaimedChunk fromChunk = LocalChunkService.getInstance().getClaimedChunk(event.getFrom().getChunk());
        if (event.getTo() == null) {
            return;
        }
        ClaimedChunk toChunk = LocalChunkService.getInstance().getClaimedChunk(event.getTo().getChunk());

        // if moving from unclaimed land into claimed land
        if (fromChunk == null && toChunk != null) {
            player.sendMessage(ChatColor.GREEN + "Entering " + toChunk.getFief());
            return;
        }

        // if moving from claimed land into claimed land
        if (fromChunk != null && toChunk != null) {
            // if the holders of the chunks are different
            if (!fromChunk.getFief().equalsIgnoreCase(toChunk.getFief())) {
                player.sendMessage(ChatColor.AQUA + "Entering " + toChunk.getFief());
                return;
            }
        }

        // if moving into unclaimed land
        if (fromChunk != null && toChunk == null) {
            if (MedievalFactionsIntegrator.getInstance().getAPI().isChunkClaimed(event.getTo().getChunk())) {
                player.sendMessage(ChatColor.AQUA + "Leaving " + fromChunk.getFief());
            }
        }

    }

    @EventHandler()
    public void handle(BlockFromToEvent event) {
        // this event handler method will deal with liquid moving from one block to another

        ClaimedChunk fromChunk = LocalChunkService.getInstance().getClaimedChunk(event.getBlock().getChunk());
        ClaimedChunk toChunk = LocalChunkService.getInstance().getClaimedChunk(event.getToBlock().getChunk());

        // if moving from unclaimed land into claimed land
        if (fromChunk == null && toChunk != null) {
            event.setCancelled(true);
            return;
        }

        // if moving from claimed land into claimed land
        if (fromChunk != null && toChunk != null) {
            // if the holders of the chunks are different
            if (!fromChunk.getFief().equalsIgnoreCase(toChunk.getFief())) {
                event.setCancelled(true);
            }
        }

    }
}