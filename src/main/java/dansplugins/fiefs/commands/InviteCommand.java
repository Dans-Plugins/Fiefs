package dansplugins.fiefs.commands;

import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.Fief;
import dansplugins.fiefs.utils.UUIDChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class InviteCommand {

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
            player.sendMessage(ChatColor.RED + "Usage: /fiefs invite (playerName)");
            return false;
        }

        String targetName = args[0];

        if (targetName.equalsIgnoreCase(player.getName())) {
            player.sendMessage(ChatColor.RED + "You can't invite yourself.");
            return false;
        }

        UUID targetUUID = UUIDChecker.getInstance().findUUIDBasedOnPlayerName(targetName);

        if (targetUUID == null) {
            player.sendMessage(ChatColor.RED+ "That player wasn't found.");
            return false;
        }

        MF_Faction targetsFaction = MedievalFactionsIntegrator.getInstance().getAPI().getFaction(targetUUID);
        if (targetsFaction == null || !targetsFaction.getName().equalsIgnoreCase(playersFaction.getName())) {
            player.sendMessage(ChatColor.RED + "'" + targetName + "'is not in your faction.");
            return false;
        }

        Fief targetsFief = PersistentData.getInstance().getFief(targetName);
        if (targetsFief != null) {
            player.sendMessage(ChatColor.RED + "That player is already in " + targetsFief.getName());
            return false;
        }

        playersFief.invitePlayer(targetUUID);

        Player target = Bukkit.getServer().getPlayer(targetUUID);
        if (target != null) {
            target.sendMessage(ChatColor.AQUA + "You have been invited to " + playersFief.getName() + ". Type /fiefs join (fiefName) to join.");
        }
        player.sendMessage(ChatColor.GREEN + "Invited.");
        return true;
    }

}
