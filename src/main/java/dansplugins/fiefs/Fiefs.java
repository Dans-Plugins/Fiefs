package dansplugins.fiefs;

import dansplugins.fiefs.managers.StorageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class Fiefs extends JavaPlugin {

    private static Fiefs instance;

    private final boolean debug = true;

    private final String version = "v0.2";

    public static Fiefs getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        if (!MedievalFactionsIntegrator.getInstance().isMedievalFactionsPresent()) {
            if (debug) {
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
        return debug;
        // return getConfig().getBoolean("debugMode");
    }

    private boolean isVersionMismatched() {
        return !getConfig().getString("version").equalsIgnoreCase(getVersion());
    }


}
