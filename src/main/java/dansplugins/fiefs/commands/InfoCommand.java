package dansplugins.fiefs.commands;

import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.fiefs.MedievalFactionsIntegrator;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.Fief;
import dansplugins.fiefs.utils.UUIDChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand {

    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        MF_Faction faction = MedievalFactionsIntegrator.getInstance().getAPI().getFaction(player);
        if (faction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to use this command.");
            return false;
        }

        if (args.length > 0) {
            String fiefName = args[0];
            Fief fief = PersistentData.getInstance().getFief(fiefName);
            if (fief == null) {
                player.sendMessage(ChatColor.RED + "That fief wasn't found.");
                return false;
            }

            PersistentData.getInstance().sendFiefInfoToPlayer(player, fief);
            return true;
        }

        Fief playersFief = PersistentData.getInstance().getFief(player);
        if (playersFief == null) {
            player.sendMessage(ChatColor.RED + "You must be in a fief to use this command.");
            return false;
        }

        PersistentData.getInstance().sendFiefInfoToPlayer(player, playersFief);
        return true;
    }

}
