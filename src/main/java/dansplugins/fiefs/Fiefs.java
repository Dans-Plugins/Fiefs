package dansplugins.fiefs;

import dansplugins.factionsystem.eventhandlers.JoinHandler;
import dansplugins.fiefs.bstats.Metrics;
import dansplugins.fiefs.commands.*;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.externalapi.FiefsAPI;
import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import dansplugins.fiefs.services.ChunkService;
import dansplugins.fiefs.services.ConfigService;
import dansplugins.fiefs.services.StorageService;
import dansplugins.fiefs.utils.Logger;
import dansplugins.fiefs.utils.Scheduler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import preponderous.ponder.minecraft.bukkit.PonderMC;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.abs.PonderBukkitPlugin;
import preponderous.ponder.minecraft.bukkit.services.CommandService;
import preponderous.ponder.minecraft.bukkit.tools.EventHandlerRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public final class Fiefs extends PonderBukkitPlugin {
    private final String pluginVersion = "v" + getDescription().getVersion();

    private final CommandService commandService = new CommandService((PonderMC) getPonder());
    private final Logger logger = new Logger(this);
    private final MedievalFactionsIntegrator medievalFactionsIntegrator = new MedievalFactionsIntegrator(logger);
    private final ConfigService configService = new ConfigService(this);
    private final PersistentData persistentData = new PersistentData(medievalFactionsIntegrator);
    private final StorageService storageService = new StorageService(configService, this, persistentData, logger, medievalFactionsIntegrator);
    private final Scheduler scheduler = new Scheduler(logger, this, storageService);
    private final ChunkService chunkService = new ChunkService(persistentData, medievalFactionsIntegrator);

    /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        initializeConfig();

        if (!medievalFactionsIntegrator.isMedievalFactionsAPIAvailable()) {
            logger.log("Fiefs cannot enable.");
            return;
        }

        storageService.load();
        registerEventHandlers();
        initializeCommandService();
        scheduler.scheduleAutosave();
        handlebStatsIntegration();
    }

    /**
     * This runs when the server stops.
     */
    @Override
    public void onDisable() {
        storageService.save();
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
            DefaultCommand defaultCommand = new DefaultCommand(this);
            return defaultCommand.execute(sender);
        }

        return commandService.interpretAndExecuteCommand(sender, label, args);
    }

    /**
     * This can be used to get the version of the plugin.
     * @return A string containing the version preceded by 'v'
     */
    public String getVersion() {
        return pluginVersion;
    }

    /**
     * Checks if the version is mismatched.
     * @return A boolean indicating if the version is mismatched.
     */
    public boolean isVersionMismatched() {
        String configVersion = this.getConfig().getString("version");
        if (configVersion == null || this.getVersion() == null) {
            return false;
        } else {
            return !configVersion.equalsIgnoreCase(this.getVersion());
        }
    }

    /**
     * Checks if debug is enabled.
     * @return Whether debug is enabled.
     */
    public boolean isDebugEnabled() {
        return configService.getBoolean("debugMode");
    }

    public FiefsAPI getAPI() {
        return new FiefsAPI(persistentData);
    }

    public PonderMC getPonderMC() {
        return (PonderMC) getPonder();
    }

    private void initializeConfig() {
        if (!(new File("./plugins/Fiefs/config.yml").exists())) {
            configService.saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                configService.saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }
    }

    private void handlebStatsIntegration() {
        int pluginId = 12743;
        new Metrics(this, pluginId);
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
                new CheckClaimCommand(persistentData, chunkService),
                new ClaimCommand(medievalFactionsIntegrator, persistentData, chunkService),
                new ConfigCommand(configService),
                new CreateCommand(medievalFactionsIntegrator, persistentData, logger),
                new DescCommand(medievalFactionsIntegrator, persistentData),
                new DisbandCommand(medievalFactionsIntegrator, persistentData),
                new FlagsCommand(medievalFactionsIntegrator, persistentData),
                new HelpCommand(),
                new InfoCommand(medievalFactionsIntegrator, persistentData),
                new InviteCommand(medievalFactionsIntegrator, persistentData),
                new JoinCommand(medievalFactionsIntegrator, persistentData),
                new KickCommand(medievalFactionsIntegrator, persistentData),
                new LeaveCommand(medievalFactionsIntegrator, persistentData),
                new ListCommand(medievalFactionsIntegrator, persistentData),
                new MembersCommand(medievalFactionsIntegrator, persistentData),
                new TransferCommand(medievalFactionsIntegrator, persistentData),
                new UnclaimCommand(medievalFactionsIntegrator, persistentData, chunkService)
        ));
        commandService.initialize(commands, "That command wasn't found.");
    }
}