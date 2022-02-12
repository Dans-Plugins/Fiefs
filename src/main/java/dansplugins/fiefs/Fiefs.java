package dansplugins.fiefs;

import dansplugins.factionsystem.eventhandlers.JoinHandler;
import dansplugins.factionsystem.utils.Logger;
import dansplugins.fiefs.bstats.Metrics;
import dansplugins.fiefs.commands.*;
import dansplugins.fiefs.externalapi.FiefsAPI;
import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import dansplugins.fiefs.services.LocalConfigService;
import dansplugins.fiefs.services.LocalStorageService;
import dansplugins.fiefs.utils.Scheduler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import preponderous.ponder.minecraft.bukkit.PonderMC;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.abs.PonderBukkitPlugin;
import preponderous.ponder.minecraft.bukkit.tools.EventHandlerRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public final class Fiefs extends PonderBukkitPlugin {
    private static Fiefs instance;
    private final String pluginVersion = "v" + getDescription().getVersion();

    public static Fiefs getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        handlebStatsIntegration();
        initializeConfig();

        if (!MedievalFactionsIntegrator.getInstance().isMedievalFactionsAPIAvailable()) {
            Logger.getInstance().log("Fiefs cannot enable.");
            return;
        }

        LocalStorageService.getInstance().load();
        registerEventHandlers();
        initializeCommandService();
        Scheduler.getInstance().scheduleAutosave();
    }

    @Override
    public void onDisable() {
        LocalStorageService.getInstance().save();
    }

    /**
     * This method handles commands sent to the minecraft server and interprets them if the label matches one of the core commands.
     * @param sender The sender of the command.
     * @param cmd The command that was sent. This is unused.
     * @param label The core command that has been invoked.
     * @param args Arguments of the core command. Often sub-commands.
     * @return A boolean indicating whether the execution of the command was successful.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand();
            return defaultCommand.execute(sender);
        }

        return getPonderMC().getCommandService().interpretAndExecuteCommand(sender, label, args);
    }

    public String getVersion() {
        return pluginVersion;
    }

    public boolean isDebugEnabled() {
        return LocalConfigService.getInstance().getBoolean("debugMode");
        // return getConfig().getBoolean("debugMode");
    }

    public FiefsAPI getAPI() {
        return new FiefsAPI();
    }

    public PonderMC getPonderMC() {
        return (PonderMC) getPonder();
    }

    private void initializeConfig() {
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
    }

    private void handlebStatsIntegration() {
        int pluginId = 12743;
        new Metrics(this, pluginId);
    }

    private boolean isVersionMismatched() {
        return !getConfig().getString("version").equalsIgnoreCase(getVersion());
    }

    /**
     * Registers the event handlers of the plugin using Ponder.
     */
    private void registerEventHandlers() {
        EventHandlerRegistry eventHandlerRegistry = new EventHandlerRegistry();
        ArrayList<Listener> listeners = new ArrayList<>(Arrays.asList(
                new JoinHandler()
        ));
        eventHandlerRegistry.registerEventHandlers(listeners, this);
    }

    /**
     * Initializes Ponder's command service with the plugin's commands.
     */
    private void initializeCommandService() {
        ArrayList<AbstractPluginCommand> commands = new ArrayList<AbstractPluginCommand>(Arrays.asList(
                new CheckClaimCommand(),
                new ClaimCommand(),
                new ConfigCommand(),
                new CreateCommand(),
                new DescCommand(),
                new DisbandCommand(),
                new FlagsCommand(),
                new HelpCommand(),
                new InfoCommand(),
                new InviteCommand(),
                new JoinCommand(),
                new KickCommand(),
                new LeaveCommand(),
                new ListCommand(),
                new MembersCommand(),
                new TransferCommand(),
                new UnclaimCommand()
        ));
        getPonderMC().getCommandService().initialize(commands, "That command wasn't found.");
    }
}