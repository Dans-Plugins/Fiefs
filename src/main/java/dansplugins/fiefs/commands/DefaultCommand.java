package dansplugins.fiefs.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.tools.PermissionChecker;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class DefaultCommand extends AbstractPluginCommand {
    private final PermissionChecker permissionChecker = new PermissionChecker();
    private final String version;

    public DefaultCommand(String version) {
        super(new ArrayList<>(Arrays.asList("default")), new ArrayList<>(Arrays.asList("fiefs.default")));
        this.version = version;
    }

    /**
     * Bare {@code /fi} is not one of the commands handed to the command service, so the permission
     * check that service makes for every other command has to be made here.
     */
    @Override
    public boolean execute(CommandSender sender) {
        if (!permissionChecker.checkPermission(sender, getPermissions())) {
            return false;
        }
        sender.sendMessage(ChatColor.AQUA + "Fiefs " + version);
        sender.sendMessage(ChatColor.AQUA + "Developer: Daniel McCoy Stephenson");
        sender.sendMessage(ChatColor.AQUA + "Requested by: Laughingspade");
        sender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/Dans-Plugins/Fiefs/wiki");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}