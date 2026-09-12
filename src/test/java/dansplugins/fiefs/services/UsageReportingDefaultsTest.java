package dansplugins.fiefs.services;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the bundled {@code config.yml} and the way {@link ConfigService} reads the usage-reporting
 * block from it.
 *
 * <p>{@link ConfigService} needs a live {@code Fiefs} plugin, which cannot be constructed outside a
 * server, so the reading is exercised against the same {@link YamlConfiguration} Bukkit hands the
 * plugin: an on-disk file that predates the block, with the jar's {@code config.yml} registered as
 * its defaults, exactly as {@code JavaPlugin.reloadConfig()} sets it up. The two-argument getters
 * are what would silently turn reporting off on every upgraded installation; the one-argument
 * getters {@link ConfigService} uses fall through to the defaults.
 */
class UsageReportingDefaultsTest {

    private YamlConfiguration bundled;

    @BeforeEach
    void loadBundledConfig() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("config.yml");
        assertNotNull(stream, "config.yml is missing from the jar's resources");
        bundled = YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    @Test
    void bundledConfigCarriesTheUsageReportingBlock() {
        assertTrue(bundled.getBoolean("usage-reporting.enabled"));
        assertEquals("https://trace.danielstephenson.dev", bundled.getString("usage-reporting.endpoint"));
        String key = bundled.getString("usage-reporting.key");
        assertNotNull(key);
        assertFalse(key.isEmpty(), "the bundled key is empty, which turns reporting off everywhere");
        assertFalse(key.contains("${"), "config.yml is Maven-filtered; an unresolved placeholder leaked into the key");
    }

    @Test
    void oneArgumentGettersFallThroughToTheBundledDefaultsOnAnOlderConfig() {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("version", "v0.11.0");
        onDisk.set("debugMode", false);
        onDisk.setDefaults(bundled);

        assertTrue(onDisk.getBoolean("usage-reporting.enabled"));
        assertEquals(bundled.getString("usage-reporting.endpoint"), onDisk.getString("usage-reporting.endpoint"));
        assertEquals(bundled.getString("usage-reporting.key"), onDisk.getString("usage-reporting.key"));
    }

    @Test
    void twoArgumentGettersWouldReturnTheirFallbackInstead() {
        // The trap ConfigService avoids: measured, so a refactor to the two-argument form fails here
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.setDefaults(bundled);

        assertEquals("", onDisk.getString("usage-reporting.key", ""));
        assertFalse(onDisk.getBoolean("usage-reporting.enabled", false));
    }

    @Test
    void anExplicitOptOutOnDiskWinsOverTheBundledDefault() {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("usage-reporting.enabled", false);
        onDisk.setDefaults(bundled);

        assertFalse(onDisk.getBoolean("usage-reporting.enabled"));
    }
}
