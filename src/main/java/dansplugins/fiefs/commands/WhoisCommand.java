package dansplugins.fiefs.commands;

import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.Fief;
import dansplugins.fiefs.utils.UUIDChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public class WhoisCommand extends AbstractPluginCommand {
    private final PersistentData persistentData;

    public WhoisCommand(PersistentData persistentData) {
        super(new ArrayList<>(Arrays.asList("whois")), new ArrayList<>(Arrays.asList("fiefs.whois")));
        this.persistentData = persistentData;
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Usage: /fi whois (playerName)");
        return false;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return execute(sender);
        }

        String targetName = args[0];

        UUIDChecker uuidChecker = new UUIDChecker();
        UUID targetUUID = uuidChecker.findUUIDBasedOnPlayerName(targetName);
        if (targetUUID == null) {
            sender.sendMessage(ChatColor.RED + "That player wasn't found.");
            return false;
        }

        Fief targetsFief = persistentData.getFief(targetUUID);
        if (targetsFief == null) {
            sender.sendMessage(ChatColor.AQUA + targetName + " is not a member of a fief.");
            return true;
        }

        sender.sendMessage(ChatColor.AQUA + targetName + " is a member of " + targetsFief.getName() + ".");
        return true;
    }
}
