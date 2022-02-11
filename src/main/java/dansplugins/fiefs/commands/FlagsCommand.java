package dansplugins.fiefs.commands;

import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.Fief;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlagsCommand {
    
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

        Fief playersFief = PersistentData.getInstance().getFief(player);
        if (playersFief == null) {
            player.sendMessage(ChatColor.RED + "You must be in a fief to use this command.");
            return false;
        }

        if (!playersFief.getOwnerUUID().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be the owner of your fief to kick members.");
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Valid sub-commands: show, set");
            return false;
        }

        if (args[0].equalsIgnoreCase("show")) {
            playersFief.getFlags().sendFlagList(player);
        }
        else if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Usage: /fi flags set (flag) (value)");
                return false;
            }
            else {
                playersFief.getFlags().setFlag(args[1], args[2], player);
            }
        }
        else {
            player.sendMessage(ChatColor.RED + "Valid sub-commands: show, set");
        }
        return true;
    }

}
