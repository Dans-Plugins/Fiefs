package dansplugins.fiefs.commands;

import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.Fief;
import dansplugins.fiefs.utils.UUIDChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TransferCommand {

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        MF_Faction playersFaction = MedievalFactionsIntegrator.getInstance().getAPI().getFaction(player);
        if (playersFaction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to use this command.");
            return false;
        }

        Fief playersFief = PersistentData.getInstance().getFief(player);
        if (playersFief == null) {
            player.sendMessage(ChatColor.RED + "You must be in a fief to use this command.");
            return false;
        }

        if (!playersFief.getOwnerUUID().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be the owner of your fief to invite others.");
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /fiefs transfer (playerName)");
            return false;
        }

        String targetName = args[0];

        if (targetName.equalsIgnoreCase(player.getName())) {
            player.sendMessage(ChatColor.RED + "You can't transfer your faction to yourself.");
            return false;
        }

        UUID targetUUID = UUIDChecker.getInstance().findUUIDBasedOnPlayerName(targetName);
        if (targetUUID == null) {
            player.sendMessage(ChatColor.RED + "That player wasn't found.");
            return false;
        }

        if (!playersFief.isMember(targetUUID)) {
            player.sendMessage(ChatColor.RED + "That player is not in your fief.");
            return false;
        }

        playersFief.setOwnerUUID(targetUUID);
        player.sendMessage(ChatColor.GREEN + "Transfered.");

        // TODO: inform fief members about transfer of power

        return true;
    }

}
