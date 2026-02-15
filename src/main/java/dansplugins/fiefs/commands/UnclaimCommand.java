package dansplugins.fiefs.commands;

import com.dansplugins.factionsystem.faction.MfFaction;
import com.dansplugins.factionsystem.player.MfPlayer;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import dansplugins.fiefs.objects.Fief;
import dansplugins.fiefs.services.ChunkService;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class UnclaimCommand extends AbstractPluginCommand {
    private final MedievalFactionsIntegrator medievalFactionsIntegrator;
    private final PersistentData persistentData;
    private final ChunkService chunkService;

    public UnclaimCommand(MedievalFactionsIntegrator medievalFactionsIntegrator, PersistentData persistentData, ChunkService chunkService) {
        super(new ArrayList<>(Arrays.asList("unclaim")), new ArrayList<>(Arrays.asList("fiefs.unclaim")));
        this.medievalFactionsIntegrator = medievalFactionsIntegrator;
        this.persistentData = persistentData;
        this.chunkService = chunkService;
    }

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        MfPlayer mfPlayer = medievalFactionsIntegrator.getAPI().getServices().getPlayerService().getPlayer(player);
        if (mfPlayer == null) {
            player.sendMessage(ChatColor.RED + "Could not load your player data.");
            return false;
        }

        MfFaction faction = medievalFactionsIntegrator.getAPI().getServices().getFactionService().getFaction(mfPlayer.getId());
        if (faction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to use this command.");
            return false;
        }

        Fief playersFief = persistentData.getFief(player);
        if (playersFief == null) {
            player.sendMessage(ChatColor.RED + "You must be in a fief to use this command.");
            return false;
        }

        Chunk chunk = player.getLocation().getChunk();
        return chunkService.attemptToUnclaimChunk(chunk, playersFief, player);
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}