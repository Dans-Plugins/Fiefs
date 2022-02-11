package dansplugins.fiefs;

import dansplugins.fiefs.bstats.Metrics;
import dansplugins.fiefs.externalapi.FiefsAPI;
import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import dansplugins.fiefs.services.LocalConfigService;
import dansplugins.fiefs.services.LocalCommandService;
import dansplugins.fiefs.services.LocalStorageService;
import dansplugins.fiefs.utils.EventRegistry;
import dansplugins.fiefs.utils.Scheduler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Fiefs extends JavaPlugin {

    private static Fiefs instance;

    private final String version = "v0.10-alpha-1";

    public static Fiefs getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        int pluginId = 12743;
        Metrics metrics = new Metrics(this, pluginId);

        if (!(new File("./plugins/Fiefs/config.yml").exists())) {
            LocalConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                LocalConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }

        if (!MedievalFactionsIntegrator.getInstance().isMedievalFactionsAPIAvailable()) {
            System.out.println("[Fiefs] Fiefs cannot enable.");
            return;
        }

        LocalStorageService.getInstance().load();

        EventRegistry.getInstance().registerEvents();

        Scheduler.getInstance().scheduleAutosave();
    }

    @Override
    public void onDisable() {
        LocalStorageService.getInstance().save();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        LocalCommandService localCommandService = new LocalCommandService();
        return localCommandService.interpretCommand(sender, label, args);
    }

    public String getVersion() {
        return version;
    }

    public boolean isDebugEnabled() {
        return LocalConfigService.getInstance().getBoolean("debugMode");
        // return getConfig().getBoolean("debugMode");
    }

    public FiefsAPI getAPI() {
        return new FiefsAPI();
    }

    private boolean isVersionMismatched() {
        return !getConfig().getString("version").equalsIgnoreCase(getVersion());
    }


}
