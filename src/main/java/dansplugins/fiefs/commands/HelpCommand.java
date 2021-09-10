package dansplugins.fiefs.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpCommand {

    public boolean execute(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Fiefs Commands ===");
        sender.sendMessage(ChatColor.AQUA + "/fi help");
        sender.sendMessage(ChatColor.AQUA + "/fi list");
        sender.sendMessage(ChatColor.AQUA + "/fi join");
        sender.sendMessage(ChatColor.AQUA + "/fi info");
        sender.sendMessage(ChatColor.AQUA + "/fi leave");
        sender.sendMessage(ChatColor.AQUA + "/fi create");
        sender.sendMessage(ChatColor.AQUA + "/fi invite");
        sender.sendMessage(ChatColor.AQUA + "/fi disband");
        sender.sendMessage(ChatColor.AQUA + "/fi claim");
        sender.sendMessage(ChatColor.AQUA + "/fi unclaim");
        sender.sendMessage(ChatColor.AQUA + "/fi checkclaim");
        sender.sendMessage(ChatColor.AQUA + "/fi desc");
        sender.sendMessage(ChatColor.AQUA + "/fi members");
        sender.sendMessage(ChatColor.AQUA + "/fi kick");
        return true;
    }

}
