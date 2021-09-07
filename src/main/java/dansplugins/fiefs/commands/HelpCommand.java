package dansplugins.fiefs.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpCommand {

    public boolean execute(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Fiefs Commands ===");
        sender.sendMessage(ChatColor.AQUA + "/mf help");
        sender.sendMessage(ChatColor.AQUA + "/mf list");
        sender.sendMessage(ChatColor.AQUA + "/mf create");
        sender.sendMessage(ChatColor.AQUA + "/mf disband");
        return true;
    }

}
