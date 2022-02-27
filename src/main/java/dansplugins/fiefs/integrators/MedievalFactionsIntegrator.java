package dansplugins.fiefs.integrators;

import org.bukkit.Bukkit;

import dansplugins.factionsystem.externalapi.MedievalFactionsAPI;
import dansplugins.fiefs.utils.Logger;

/**
 * @author Daniel McCoy Stephenson
 */
public class MedievalFactionsIntegrator {
    private static MedievalFactionsIntegrator instance;
    private MedievalFactionsAPI mf_api = null;

    private MedievalFactionsIntegrator() {
        if (isMedievalFactionsPresent()) {
            Logger.getInstance().log("[DEBUG] Medieval Factions was found successfully!");
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

    public static MedievalFactionsIntegrator getInstance() {
        if (instance == null) {
            instance = new MedievalFactionsIntegrator();
        }
        return instance;
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