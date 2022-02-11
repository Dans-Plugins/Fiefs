package dansplugins.fiefs.commands;

import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.ClaimedChunk;
import dansplugins.fiefs.objects.Fief;
import dansplugins.fiefs.services.LocalChunkService;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckClaimCommand {

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        Fief playersFief = PersistentData.getInstance().getFief(player);

        Chunk chunk = player.getLocation().getChunk();
        ClaimedChunk claimedChunk = LocalChunkService.getInstance().getClaimedChunk(chunk);
        if (claimedChunk != null) {
            player.sendMessage(ChatColor.AQUA + "This land is claimed by " + playersFief.getName() + " and is located in " + playersFief.getFactionName());
        }
        else {
            player.sendMessage(ChatColor.GREEN + "This land is currently not claimed by a fief.");
        }
        return true;
    }

}
