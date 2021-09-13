package dansplugins.fiefs.commands;

import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.Fief;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FlagsCommand {
    
    public void execute(Player player, String[] args, String key) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Valid sub-commands: show, set");
            return;
        }

        final Fief playersFief = PersistentData.getInstance().getFief(player);

        if (args[0].equalsIgnoreCase("show")) {
            playersFief.getFlags().sendFlagList(player);
        }
        else if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Usage: /fi flags set (flag) (value)");
            }
            else {
                playersFief.getFlags().setFlag(args[1], args[2], player);
            }
        }
        else {
            player.sendMessage(ChatColor.RED + "Valid sub-commands: show, set");
        }
    }

}
