package dansplugins.fiefs.integrators;

import com.dansplugins.factionsystem.MedievalFactions;
import com.dansplugins.factionsystem.faction.MfFaction;
import com.dansplugins.factionsystem.player.MfPlayer;
import dansplugins.fiefs.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author Daniel McCoy Stephenson
 */
public class MedievalFactionsIntegrator {

    private MedievalFactions medievalFactions = null;

    public MedievalFactionsIntegrator(Logger logger) {
        if (isMedievalFactionsPresent()) {
            logger.log("[DEBUG] Medieval Factions was found successfully!");
            try {
                Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("MedievalFactions");
                if (plugin instanceof MedievalFactions) {
                    medievalFactions = (MedievalFactions) plugin;
                }
            }
            catch(NoClassDefFoundError e) {
                System.out.println("[Fiefs] There was a problem accessing Medieval Factions. Medieval Factions might need to be updated.");
            }
        }
        else {
            System.out.println("[Fiefs] Medieval Factions was not found!");
        }
    }

    public boolean isMedievalFactionsAPIAvailable() {
        return isMedievalFactionsPresent() && medievalFactions != null;
    }

    private boolean isMedievalFactionsPresent() {
        return (Bukkit.getServer().getPluginManager().getPlugin("MedievalFactions") != null);
    }

    public MedievalFactions getAPI() {
        return medievalFactions;
    }

    /**
     * Resolves the MF faction a player belongs to, sending the player a
     * standard error message and returning null if that isn't possible.
     * Centralizes a lookup that was previously duplicated across every command.
     */
    public MfFaction getFactionForPlayer(Player player) {
        MfPlayer mfPlayer = getAPI().getServices().getPlayerService().getPlayerByBukkitPlayer(player);
        if (mfPlayer == null) {
            player.sendMessage(ChatColor.RED + "Could not load your player data.");
            return null;
        }

        MfFaction faction = getAPI().getServices().getFactionService().getFactionByPlayerId(mfPlayer.getId());
        if (faction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to use this command.");
        }
        return faction;
    }
}