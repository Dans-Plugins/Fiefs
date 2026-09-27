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

    private final Logger logger;

    private MedievalFactions medievalFactions = null;

    /**
     * Only stores the logger. The Medieval Factions lookup happens in {@link #initialize()},
     * because this object is built while the main class is still being constructed: the
     * logger reads the config through the plugin, and the config service does not exist yet
     * at that point. Logging here threw a NullPointerException on any server that had
     * Medieval Factions installed, and the plugin never loaded (#188).
     */
    public MedievalFactionsIntegrator(Logger logger) {
        this.logger = logger;
    }

    /**
     * Looks Medieval Factions up through the plugin manager and keeps hold of it. Call from
     * {@code onEnable()}, once the config has been read, and before anything that needs the API.
     */
    public void initialize() {
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
