package dansplugins.fiefs.commands;

import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.factionsystem.utils.UUIDChecker;
import dansplugins.fiefs.MedievalFactionsIntegrator;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.Fief;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand {

    public boolean execute(CommandSender sender) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        MF_Faction faction = MedievalFactionsIntegrator.getInstance().getAPI().getFaction(player);
        if (faction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to use this command.");
            return false;
        }

        Fief playersFief = PersistentData.getInstance().getFief(player);
        if (playersFief == null) {
            player.sendMessage(ChatColor.RED + "You must be in a fief to use this command.");
            return false;
        }

        int cumulativePowerLevel = playersFief.getCumulativePowerLevel();

        player.sendMessage(ChatColor.AQUA + "=== " + playersFief.getName() + " ===");
        player.sendMessage(ChatColor.AQUA + "Name: " + playersFief.getName());
        player.sendMessage(ChatColor.AQUA + "Faction: " + playersFief.getFactionName());
        player.sendMessage(ChatColor.AQUA + "Owner: " + UUIDChecker.getInstance().findPlayerNameBasedOnUUID(playersFief.getOwnerUUID()));
        player.sendMessage(ChatColor.AQUA + "Members: " + playersFief.getNumMembers());
        player.sendMessage(ChatColor.AQUA + "Power Level: " + cumulativePowerLevel);
        player.sendMessage(ChatColor.AQUA + "Demesne Size: " + PersistentData.getInstance().getNumChunksClaimedByFief(playersFief) + "/" + cumulativePowerLevel);
        return true;
    }

}
