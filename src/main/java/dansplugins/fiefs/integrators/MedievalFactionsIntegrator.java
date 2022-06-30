package dansplugins.fiefs.integrators;

import dansplugins.factionsystem.externalapi.MedievalFactionsAPI;
import dansplugins.fiefs.utils.Logger;
import org.bukkit.Bukkit;

/**
 * @author Daniel McCoy Stephenson
 */
public class MedievalFactionsIntegrator {

    private MedievalFactionsAPI mf_api = null;

    public MedievalFactionsIntegrator(Logger logger) {
        if (isMedievalFactionsPresent()) {
            logger.log("[DEBUG] Medieval Factions was found successfully!");
            try {
                mf_api = new MedievalFactionsAPI();
            }
            catch(NoClassDefFoundError e) {
                System.out.println("[Fiefs] There was a problem instantiating the Medieval Factions API. Medieval Factions might need to be updated.");
            }
        }
        else {
            System.out.println("[Fiefs] Medieval Factions was not found!");
        }
    }

    public boolean isMedievalFactionsAPIAvailable() {
        return isMedievalFactionsPresent() && mf_api != null;
    }

    private boolean isMedievalFactionsPresent() {
        return (Bukkit.getServer().getPluginManager().getPlugin("MedievalFactions") != null);
    }

    public MedievalFactionsAPI getAPI() {
        return mf_api;
    }
}