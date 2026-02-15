package dansplugins.fiefs.integrators;

import com.dansplugins.factionsystem.MedievalFactions;
import dansplugins.fiefs.utils.Logger;
import org.bukkit.Bukkit;
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
}