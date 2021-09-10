package dansplugins.fiefs;

import dansplugins.fiefs.managers.ConfigManager;
import dansplugins.fiefs.managers.StorageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Fiefs extends JavaPlugin {

    private static Fiefs instance;

    private final String version = "v0.4";

    public static Fiefs getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        if (!(new File("./plugins/Fiefs/config.yml").exists())) {
            ConfigManager.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                ConfigManager.getInstance().saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }

        if (!MedievalFactionsIntegrator.getInstance().isMedievalFactionsPresent()) {
            if (isDebugEnabled()) {
                System.out.println("Medieval Factions wasn't found. Fiefs cannot enable.");
            }
            return;
        }

        StorageManager.getInstance().load();

        EventRegistry.getInstance().registerEvents();
    }

    @Override
    public void onDisable() {
        StorageManager.getInstance().save();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        CommandInterpreter commandInterpreter = new CommandInterpreter();
        return commandInterpreter.interpretCommand(sender, label, args);
    }

    public String getVersion() {
        return version;
    }

    public boolean isDebugEnabled() {
        return ConfigManager.getInstance().getBoolean("debugMode");
        // return getConfig().getBoolean("debugMode");
    }

    private boolean isVersionMismatched() {
        return !getConfig().getString("version").equalsIgnoreCase(getVersion());
    }


}
