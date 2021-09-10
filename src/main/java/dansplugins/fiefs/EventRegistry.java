package dansplugins.fiefs;

import dansplugins.fiefs.eventhandlers.FactionEventHandler;
import dansplugins.fiefs.eventhandlers.InteractionHandler;
import org.bukkit.plugin.PluginManager;

public class EventRegistry {

    private static EventRegistry instance;

    private EventRegistry() {

    }

    public static EventRegistry getInstance() {
        if (instance == null) {
            instance = new EventRegistry();
        }
        return instance;
    }

    public void registerEvents() {

        Fiefs mainInstance = Fiefs.getInstance();
        PluginManager manager = mainInstance.getServer().getPluginManager();

        manager.registerEvents(new FactionEventHandler(), mainInstance);
        manager.registerEvents(new InteractionHandler(), mainInstance);
    }
}
