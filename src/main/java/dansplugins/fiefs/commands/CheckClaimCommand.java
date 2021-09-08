package dansplugins.fiefs.commands;

import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.managers.ChunkManager;
import dansplugins.fiefs.objects.Fief;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckClaimCommand {

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            // TODO: add message
            return false;
        }

        Player player = (Player) sender;

        Fief playersFief = PersistentData.getInstance().getFief(player);

        if (playersFief == null) {
            // TODO: add message
            return false;
        }

        Chunk chunk = player.getLocation().getChunk();
        return (ChunkManager.getInstance().getClaimedChunk(chunk) != null);
    }

}
