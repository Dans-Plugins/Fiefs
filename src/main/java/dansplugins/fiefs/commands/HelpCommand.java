package dansplugins.fiefs.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpCommand {

    private int maxPage = 2;

    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsageMessage(sender);
            return false;
        }

        String page = args[0];

        switch(page) {
            case "1":
                sendPageOne(sender);
                break;
            case "2":
                sendPageTwo(sender);
                break;
            default:
                sendUsageMessage(sender);
                return false;
        }
        return true;
    }

    private void sendUsageMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Usage: /help { 1 | 2 }");
    }

    private void sendPageOne(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Fiefs Commands Page 1/" + maxPage + "===");
        sender.sendMessage(ChatColor.AQUA + "/fi help");
        sender.sendMessage(ChatColor.AQUA + "/fi list");
        sender.sendMessage(ChatColor.AQUA + "/fi join");
        sender.sendMessage(ChatColor.AQUA + "/fi info");
        sender.sendMessage(ChatColor.AQUA + "/fi leave");
        sender.sendMessage(ChatColor.AQUA + "/fi create");
        sender.sendMessage(ChatColor.AQUA + "/fi invite");
    }

    private void sendPageTwo(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Fiefs Commands Page 2/" + maxPage + "===");
        sender.sendMessage(ChatColor.AQUA + "/fi disband");
        sender.sendMessage(ChatColor.AQUA + "/fi claim");
        sender.sendMessage(ChatColor.AQUA + "/fi unclaim");
        sender.sendMessage(ChatColor.AQUA + "/fi checkclaim");
        sender.sendMessage(ChatColor.AQUA + "/fi desc");
        sender.sendMessage(ChatColor.AQUA + "/fi members");
        sender.sendMessage(ChatColor.AQUA + "/fi kick");
    }

}
