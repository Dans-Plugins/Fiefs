package dansplugins.fiefs.commands;

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

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Valid sub-commands: show, set");
            return false;
        }

        final Fief playersFief = PersistentData.getInstance().getFief(player);

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
