package dansplugins.fiefs.commands;

import com.dansplugins.factionsystem.faction.MfFaction;
import com.dansplugins.factionsystem.player.MfPlayer;
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
public class LeaveCommand extends AbstractPluginCommand {
    private final MedievalFactionsIntegrator medievalFactionsIntegrator;
    private final PersistentData persistentData;

    public LeaveCommand(MedievalFactionsIntegrator medievalFactionsIntegrator, PersistentData persistentData) {
        super(new ArrayList<>(Arrays.asList("leave")), new ArrayList<>(Arrays.asList("fiefs.leave")));
        this.medievalFactionsIntegrator = medievalFactionsIntegrator;
        this.persistentData = persistentData;
    }

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        MfPlayer mfPlayer = medievalFactionsIntegrator.getAPI().getServices().getPlayerService().getPlayerByBukkitPlayer(player);
        if (mfPlayer == null) {
            player.sendMessage(ChatColor.RED + "Could not load your player data.");
            return false;
        }

        MfFaction faction = medievalFactionsIntegrator.getAPI().getServices().getFactionService().getFactionByPlayerId(mfPlayer.getId());
        if (faction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to use this command.");
            return false;
        }

        Fief fief = persistentData.getFief(player);
        if (fief == null) {
            player.sendMessage(ChatColor.RED + "You must be in a fief to use this command.");
            return false;
        }

        if (fief.getOwnerUUID().equals(player.getUniqueId())) {
            persistentData.removeFief(fief);
            player.sendMessage(ChatColor.GREEN + "Left. Your fief was disbanded since you were the owner.");
            return true;
        }

        fief.removeMember(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Left.");

        // TODO: inform fief members that the player has left the fief

        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}