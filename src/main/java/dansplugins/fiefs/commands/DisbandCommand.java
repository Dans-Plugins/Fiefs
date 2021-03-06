package dansplugins.fiefs.commands;

import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import dansplugins.fiefs.objects.Fief;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class DisbandCommand extends AbstractPluginCommand {
    private final MedievalFactionsIntegrator medievalFactionsIntegrator;
    private final PersistentData persistentData;

    public DisbandCommand(MedievalFactionsIntegrator medievalFactionsIntegrator, PersistentData persistentData) {
        super(new ArrayList<>(Arrays.asList("disband")), new ArrayList<>(Arrays.asList("fiefs.disband")));
        this.medievalFactionsIntegrator = medievalFactionsIntegrator;
        this.persistentData = persistentData;
    }

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        MF_Faction faction = medievalFactionsIntegrator.getAPI().getFaction(player);
        if (faction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to use this command.");
            return false;
        }

        Fief fief = persistentData.getFief(player);
        if (fief == null) {
            player.sendMessage(ChatColor.RED + "You must be in a fief to use this command.");
            return false;
        }

        if (!fief.getOwnerUUID().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be the owner of your fief to disband it.");
            return false;
        }

        persistentData.removeFief(fief);
        player.sendMessage(ChatColor.GREEN + "Fief disbanded.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }

}
