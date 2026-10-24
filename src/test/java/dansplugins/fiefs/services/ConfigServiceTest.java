package dansplugins.fiefs.services;

import dansplugins.fiefs.testsupport.BukkitTestDoubles;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Characterization tests for {@link ConfigService}: how {@code /fi config set} finds, refuses and
 * coerces an option, what {@code /fi config show} prints, and the usage-reporting getters'
 * fallbacks.
 *
 * <p>{@link ConfigService} needs a live {@code Fiefs} plugin, which cannot be constructed outside a
 * server, so the service is built without one and handed a {@link YamlConfiguration} through
 * {@link ConfigService#getConfig()} instead. The paths that never save run to completion. A
 * successful set saves through the plugin after setting the value and replying, so those tests
 * check the value and the reply and expect the save to fail for want of a plugin; whether the
 * file is then written is not exercised here.
 */
class ConfigServiceTest {

    private final YamlConfiguration config = new YamlConfiguration();
    private final ConfigService configService = new ConfigService(null) {
        @Override
        public FileConfiguration getConfig() {
            return config;
        }
    };
    private final List<String> messages = new ArrayList<>();
    private final CommandSender sender = BukkitTestDoubles.messageCapturingConsole(messages);

    private String lastMessage() {
        return messages.get(messages.size() - 1);
    }

    /** Runs a set expected to succeed, up to the save that needs the absent plugin. */
    private void setUpToTheSave(String option, String value) {
        assertThrows(NullPointerException.class, () -> configService.setConfigOption(option, value, sender),
                "a successful set saves through the plugin, which these tests do not have");
    }

    @Test
    void setConfigOption_withAnOptionThatIsNotSet_repliesNotFoundAndChangesNothing() {
        config.set("debugMode", false);

        configService.setConfigOption("noSuchOption", "true", sender);

        assertEquals(1, messages.size());
        assertTrue(lastMessage().contains("That config option wasn't found."));
        assertFalse(config.isSet("noSuchOption"));
        assertFalse(configService.hasBeenAltered());
    }

    @Test
    void setConfigOption_version_isRefusedAndLeftAsItWas() {
        config.set("version", "v1.0.0");

        configService.setConfigOption("version", "v9.9.9", sender);

        assertEquals(1, messages.size());
        assertTrue(lastMessage().contains("Cannot set version."));
        assertEquals("v1.0.0", config.getString("version"));
        assertFalse(configService.hasBeenAltered());
    }

    @Test
    void setConfigOption_findsTheOptionCaseSensitively() {
        // ConfigCommand passes the option through as typed, and the lookup here does not fold case
        config.set("debugMode", false);

        configService.setConfigOption("DEBUGMODE", "true", sender);

        assertTrue(lastMessage().contains("That config option wasn't found."));
        assertFalse(config.getBoolean("debugMode"));
    }

    @Test
    void setConfigOption_debugMode_storesABoolean() {
        config.set("debugMode", false);

        setUpToTheSave("debugMode", "true");

        assertEquals(Boolean.TRUE, config.get("debugMode"));
        assertTrue(lastMessage().contains("Boolean set."));
    }

    @Test
    void setConfigOption_limitLandAndEnableTerritoryAlerts_storeBooleans() {
        config.set("limitLand", true);
        config.set("enableTerritoryAlerts", true);

        setUpToTheSave("limitLand", "false");
        setUpToTheSave("enableTerritoryAlerts", "false");

        assertEquals(Boolean.FALSE, config.get("limitLand"));
        assertEquals(Boolean.FALSE, config.get("enableTerritoryAlerts"));
    }

    @Test
    void setConfigOption_aBooleanOption_takesAnythingButTrueAsFalse() {
        // Boolean.parseBoolean: no error for a typo, the option is just turned off
        config.set("enableTerritoryAlerts", true);

        setUpToTheSave("enableTerritoryAlerts", "yes");

        assertEquals(Boolean.FALSE, config.get("enableTerritoryAlerts"));
        assertTrue(lastMessage().contains("Boolean set."));
    }

    @Test
    void setConfigOption_aBooleanOption_parsesTheValueCaseInsensitively() {
        config.set("debugMode", false);

        setUpToTheSave("debugMode", "TRUE");

        assertEquals(Boolean.TRUE, config.get("debugMode"));
    }

    @Test
    void setConfigOption_usageReportingEnabled_storesABoolean() {
        // The per-plugin opt-out documented in CONFIG.md
        config.set("usage-reporting.enabled", true);

        setUpToTheSave("usage-reporting.enabled", "false");

        assertEquals(Boolean.FALSE, config.get("usage-reporting.enabled"));
        assertFalse(configService.isUsageReportingEnabled());
    }

    @Test
    void setConfigOption_anyOtherOption_storesTheValueAsAString() {
        config.set("usage-reporting.endpoint", "https://trace.danielstephenson.dev");

        setUpToTheSave("usage-reporting.endpoint", "https://example.invalid");

        assertEquals("https://example.invalid", config.get("usage-reporting.endpoint"));
        assertTrue(lastMessage().contains("String set."));
    }

    @Test
    void setConfigOption_withTheOptionOnlyInTheDefaults_repliesNotFound() {
        // Defaults count as set only once copyDefaults(true) is on, which
        // saveMissingConfigDefaultsIfNotPresent turns on at enable
        YamlConfiguration defaults = new YamlConfiguration();
        defaults.set("usage-reporting.enabled", true);
        config.setDefaults(defaults);

        configService.setConfigOption("usage-reporting.enabled", "false", sender);

        assertTrue(lastMessage().contains("That config option wasn't found."));
    }

    @Test
    void setConfigOption_withTheOptionOnlyInTheDefaultsAndCopyDefaultsOn_setsIt() {
        YamlConfiguration defaults = new YamlConfiguration();
        defaults.set("usage-reporting.enabled", true);
        config.setDefaults(defaults);
        config.options().copyDefaults(true);

        setUpToTheSave("usage-reporting.enabled", "false");

        assertFalse(config.getBoolean("usage-reporting.enabled"));
    }

    @Test
    void sendConfigList_sendsAHeaderAndOneLineOfValues() {
        config.set("version", "v1.0.0");
        config.set("debugMode", true);
        config.set("limitLand", false);
        config.set("enableTerritoryAlerts", true);
        config.set("usage-reporting.enabled", false);

        configService.sendConfigList(sender);

        assertEquals(2, messages.size());
        assertTrue(messages.get(0).contains("=== Config List ==="));
        assertTrue(messages.get(1).endsWith("version: v1.0.0, debugMode: true, limitLand: false, "
                + "enableTerritoryAlerts: true, usage-reporting.enabled: false"), messages.get(1));
    }

    @Test
    void sendConfigList_showsMissingBooleansAsFalseAndAMissingVersionAsNull() {
        configService.sendConfigList(sender);

        assertTrue(messages.get(1).endsWith("version: null, debugMode: false, limitLand: false, "
                + "enableTerritoryAlerts: false, usage-reporting.enabled: false"), messages.get(1));
    }

    @Test
    void hasBeenAltered_isFalseUntilASetSucceeds() {
        assertFalse(configService.hasBeenAltered());
    }

    @Test
    void getUsageReportingEndpoint_fallsBackToTheTraceServerWhenMissing() {
        assertEquals("https://trace.danielstephenson.dev", configService.getUsageReportingEndpoint());
    }

    @Test
    void getUsageReportingEndpoint_returnsTheConfiguredValue() {
        config.set("usage-reporting.endpoint", "https://example.invalid");

        assertEquals("https://example.invalid", configService.getUsageReportingEndpoint());
    }

    @Test
    void getUsageReportingKey_fallsBackToEmptyWhenMissing() {
        assertEquals("", configService.getUsageReportingKey());
    }

    @Test
    void getUsageReportingKey_returnsTheConfiguredValue() {
        config.set("usage-reporting.key", "some-key");

        assertEquals("some-key", configService.getUsageReportingKey());
    }

    @Test
    void isUsageReportingEnabled_isFalseWhenMissing() {
        assertFalse(configService.isUsageReportingEnabled());
    }

    @Test
    void typedGetters_readThroughToTheConfig() {
        config.set("someInt", 7);
        config.set("someDouble", 2.5);
        config.set("someString", "text");
        config.set("someBoolean", true);

        assertEquals(7, configService.getInt("someInt"));
        assertEquals(2.5, configService.getDouble("someDouble"));
        assertEquals("text", configService.getString("someString"));
        assertTrue(configService.getBoolean("someBoolean"));
        assertSame(config, configService.getConfig());
    }
}
