package dansplugins.fiefs.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import dansplugins.fiefs.objects.Fief;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

/**
 * @author Daniel McCoy Stephenson
 */
public class InfoCommand extends AbstractPluginCommand {

    public InfoCommand() {
        super(new ArrayList<>(Arrays.asList("info")), new ArrayList<>(Arrays.asList("fiefs.info")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        return false;
    }

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
