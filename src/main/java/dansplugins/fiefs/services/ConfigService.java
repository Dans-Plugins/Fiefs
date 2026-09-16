package dansplugins.fiefs.services;

import dansplugins.fiefs.Fiefs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

/*
    To add a new config option, the following methods must be altered:
    - saveMissingConfigDefaultsIfNotPresent()
    - setConfigOption()
    - sendConfigList()
 */

/**
 * @author Daniel McCoy Stephenson
 */
public class ConfigService {
    private static final String USAGE_REPORTING_ENABLED_KEY = "usage-reporting.enabled";
    private static final String USAGE_REPORTING_ENDPOINT_KEY = "usage-reporting.endpoint";
    private static final String USAGE_REPORTING_KEY_KEY = "usage-reporting.key";
    private static final String DEFAULT_USAGE_REPORTING_ENDPOINT = "https://trace.danielstephenson.dev";

    private final Fiefs fiefs;

    private boolean altered = false;

    public ConfigService(Fiefs fiefs) {
        this.fiefs = fiefs;
    }

    public void saveMissingConfigDefaultsIfNotPresent() {
        // set version
        if (!getConfig().isString("version")) {
            getConfig().addDefault("version", fiefs.getVersion());
        }
        else {
            getConfig().set("version", fiefs.getVersion());
        }

        // save config options
        if (!getConfig().isSet("debugMode")) {
            getConfig().set("debugMode", false);
        }
        if (!getConfig().isSet("limitLand")) {
            getConfig().set("limitLand", true);
        }
        if (!getConfig().isSet("enableTerritoryAlerts")) {
            getConfig().set("enableTerritoryAlerts", true);
        }
        // usage-reporting.* is not set here: it lives in the bundled config.yml, which Bukkit
        // registers as this file's defaults, and copyDefaults(true) below writes it out with the rest
        getConfig().options().copyDefaults(true);
        fiefs.saveConfig();
    }

    public void setConfigOption(String option, String value, CommandSender sender) {

        if (getConfig().isSet(option)) {

            if (option.equalsIgnoreCase("version")) {
                sender.sendMessage(ChatColor.RED + "Cannot set version.");
                return;
            } else if (option.equalsIgnoreCase("a")) { // no integers yet
                getConfig().set(option, Integer.parseInt(value));
                sender.sendMessage(ChatColor.GREEN + "Integer set.");
            } else if (option.equalsIgnoreCase("debugMode")
                    || option.equalsIgnoreCase("limitLand")
                    || option.equalsIgnoreCase("enableTerritoryAlerts")
                    || option.equalsIgnoreCase(USAGE_REPORTING_ENABLED_KEY)) {
                getConfig().set(option, Boolean.parseBoolean(value));
                sender.sendMessage(ChatColor.GREEN + "Boolean set.");
            } else if (option.equalsIgnoreCase("c")) { // no doubles yet
                getConfig().set(option, Double.parseDouble(value));
                sender.sendMessage(ChatColor.GREEN + "Double set.");
            } else {
                getConfig().set(option, value);
                sender.sendMessage(ChatColor.GREEN + "String set.");
            }

            // save
            fiefs.saveConfig();
            altered = true;
        } else {
            sender.sendMessage(ChatColor.RED + "That config option wasn't found.");
        }
    }

    public void sendConfigList(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Config List ===");
        sender.sendMessage(ChatColor.AQUA + "version: " + getConfig().getString("version")
                + ", debugMode: " + getBoolean("debugMode")
                + ", limitLand: " + getBoolean("limitLand")
                + ", enableTerritoryAlerts: " + getBoolean("enableTerritoryAlerts")
                + ", usage-reporting.enabled: " + isUsageReportingEnabled());
    }

    public boolean hasBeenAltered() {
        return altered;
    }

    public FileConfiguration getConfig() {
        return fiefs.getConfig();
    }

    public int getInt(String option) {
        return getConfig().getInt(option);
    }

    public boolean getBoolean(String option) {
        return getConfig().getBoolean(option);
    }

    public double getDouble(String option) {
        return getConfig().getDouble(option);
    }

    public String getString(String option) {
        return getConfig().getString(option);
    }

    // The one-argument getters, deliberately. Bukkit registers the jar's config.yml as the
    // defaults for the file on disk, and the one-argument getters fall through to them -- but
    // the two-argument getters return their explicit fallback instead, which for the key would
    // be "" and would turn reporting off wherever the block is missing from disk. Fiefs writes
    // the block out on enable when it is missing (see initializeConfig), so this only matters
    // for a hand-trimmed config.yml or a plugins directory that could not be written to.
    // Verified against YamlConfiguration, not assumed.

    public boolean isUsageReportingEnabled() {
        return getConfig().getBoolean(USAGE_REPORTING_ENABLED_KEY);
    }

    public String getUsageReportingEndpoint() {
        String endpoint = getConfig().getString(USAGE_REPORTING_ENDPOINT_KEY);
        return endpoint == null ? DEFAULT_USAGE_REPORTING_ENDPOINT : endpoint;
    }

    public String getUsageReportingKey() {
        String key = getConfig().getString(USAGE_REPORTING_KEY_KEY);
        return key == null ? "" : key;
    }
}