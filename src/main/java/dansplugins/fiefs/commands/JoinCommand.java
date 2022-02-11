package dansplugins.fiefs.commands;

import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.Fief;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand {

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        MF_Faction faction = MedievalFactionsIntegrator.getInstance().getAPI().getFaction(player);
        if (faction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to use this command.");
            return false;
        }

        Fief fief = PersistentData.getInstance().getFief(player);
        if (fief != null) {
            player.sendMessage(ChatColor.RED + "You're already in a fief.");
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /fiefs join (fiefName)");
            return false;
        }

        String fiefName = args[0];

        Fief targetFief = PersistentData.getInstance().getFief(fiefName);

        if (targetFief == null) {
            player.sendMessage(ChatColor.RED + "That fief wasn't found.");
            return false;
        }

        if (!targetFief.getFactionName().equalsIgnoreCase(faction.getName())) {
            player.sendMessage(ChatColor.RED + "That fief isn't ");
            return false;
        }

        if (!targetFief.isInvited(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are not invited to this fief.");
            return false;
        }

        targetFief.addMember(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Joined.");

        // TODO: alert fief members that the player has joined the fief

        return true;
    }

}
