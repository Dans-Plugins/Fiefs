package dansplugins.fiefs;

import dansplugins.fiefs.bstats.Metrics;
import dansplugins.fiefs.commands.*;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.externalapi.FiefsAPI;
import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import dansplugins.fiefs.listeners.FactionEventListener;
import dansplugins.fiefs.listeners.InteractionListener;
import dansplugins.fiefs.listeners.MoveListener;
import dansplugins.fiefs.services.ChunkService;
import dansplugins.fiefs.services.ConfigService;
import dansplugins.fiefs.services.StorageService;
import dansplugins.fiefs.trace.TraceClient;
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
import java.util.Collections;

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

    // A no-op until the config has been read, so a command arriving before
    // onEnable() finishes has something safe to report to.
    private TraceClient trace = TraceClient.disabled();

    /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        initializeConfig();
        initializeUsageReporting();

        // after the config is read: the integrator logs, and the logger reads the config
        medievalFactionsIntegrator.initialize();
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
        trace.close();
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
        trace.report("command", null, Collections.singletonMap("name", cmd.getName()));
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
            // write the bundled config.yml first so its comments reach the disk copy
            saveDefaultConfig();
            configService.saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks. A config.yml from before usage reporting has no
            // usage-reporting block on disk; writing the defaults out puts the switch where
            // the operator can see it, rather than only in the jar. See
            // UsageReportingDefaultsTest for the isSet()/copyDefaults semantics this relies on.
            if (isVersionMismatched() || !getConfig().isSet("usage-reporting")) {
                configService.saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }
    }

    /**
     * Builds the usage reporting client from the config: one event now, one per command. See config.yml.
     */
    private void initializeUsageReporting() {
        trace = TraceClient.builder(configService.getUsageReportingEndpoint(), getName())
                .key(configService.getUsageReportingKey())
                .enabled(configService.isUsageReportingEnabled())
                .serverWideConfig(getDataFolder().getParentFile())
                .logger(getLogger())
                .build();
        logUsageReportingStatus();
        trace.report("startup", null, Collections.singletonMap("version", getDescription().getVersion()));
    }

    /** Says on every start whether usage reporting is on, and why not when it is off. */
    private void logUsageReportingStatus() {
        if (trace.isEnabled()) {
            getLogger().info("Usage reporting is on: " + getName() + " sends its name, version and command names to "
                    + configService.getUsageReportingEndpoint() + " - nothing about players or the server. "
                    + "Turn it off with usage-reporting.enabled: false in this plugin's config.yml, "
                    + "or for every plugin with enabled: false in plugins/trace/config.yml. "
                    + "Details: https://github.com/Stephenson-Software/trace#usage-reporting");
        } else {
            getLogger().info("Usage reporting is off (" + trace.disabledReason() + ").");
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
                new MoveListener(configService, chunkService, medievalFactionsIntegrator),
                new InteractionListener(chunkService, persistentData, logger, this),
                new FactionEventListener(persistentData, medievalFactionsIntegrator.getAPI())
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
                new RenameCommand(medievalFactionsIntegrator, persistentData),
                new TransferCommand(medievalFactionsIntegrator, persistentData),
                new UnclaimCommand(medievalFactionsIntegrator, persistentData, chunkService),
                new WhoisCommand(persistentData)
        ));
        commandService.initialize(commands, "That command wasn't found.");
    }
}