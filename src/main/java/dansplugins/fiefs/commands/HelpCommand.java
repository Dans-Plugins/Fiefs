package dansplugins.fiefs.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpCommand {

    public boolean execute(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Fiefs Commands ===");
        sender.sendMessage(ChatColor.AQUA + "/fiefs help");
        sender.sendMessage(ChatColor.AQUA + "/fiefs list");
        sender.sendMessage(ChatColor.AQUA + "/fiefs create");
        sender.sendMessage(ChatColor.AQUA + "/fiefs info");
        sender.sendMessage(ChatColor.AQUA + "/fiefs disband");
        sender.sendMessage(ChatColor.AQUA + "/fiefs claim");
        sender.sendMessage(ChatColor.AQUA + "/fiefs unclaim");
        sender.sendMessage(ChatColor.AQUA + "/fiefs checkclaim");
        return true;
    }

}
