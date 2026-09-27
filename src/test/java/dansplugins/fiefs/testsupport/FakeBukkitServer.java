package dansplugins.fiefs.testsupport;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A stand-in for the Bukkit server singleton, holding the offline-player cache that
 * {@code dansplugins.fiefs.utils.UUIDChecker} reads through the static {@link Bukkit} accessors,
 * the loaded-world registry that {@code dansplugins.fiefs.listeners.FactionEventListener}
 * reads the same way when it resolves the world id on a Medieval Factions unclaim event, and
 * the plugin registry that {@code dansplugins.fiefs.integrators.MedievalFactionsIntegrator}
 * looks Medieval Factions up in through the plugin manager.
 *
 * <p>{@link Bukkit#setServer(Server)} refuses to replace a server once one is set, and Surefire
 * runs the whole suite in one JVM, so the double is installed once per JVM and its registry is
 * cleared between tests. Call {@link #install()} from a {@code @BeforeEach} — it is idempotent —
 * and register whichever players the test needs.
 *
 * <p>The registry is static because the server it backs is, so tests that use it cannot run
 * concurrently with one another: turning on Surefire's {@code parallel} option would let one
 * test's registration or reset land in the middle of another's. Surefire runs tests serially by
 * default, and this is the constraint to weigh before changing that.
 *
 * <p>Only the methods the code under test reaches are answered; anything else throws, so a test
 * that starts touching a new part of the server surface fails loudly rather than reading a null.
 */
public final class FakeBukkitServer {

    private static final Map<UUID, String> namesByUuid = new LinkedHashMap<>();
    private static final Map<UUID, String> worldNamesByUuid = new LinkedHashMap<>();
    private static final Map<String, Plugin> pluginsByName = new LinkedHashMap<>();

    private FakeBukkitServer() {
        // static methods only
    }

    /**
     * Installs the double as the Bukkit server if no server is set yet, and empties the
     * offline-player and world registries so the calling test starts from a known state.
     */
    public static void install() {
        if (Bukkit.getServer() == null) {
            Bukkit.setServer(server());
        }
        namesByUuid.clear();
        worldNamesByUuid.clear();
        pluginsByName.clear();
    }

    /**
     * Registers a loaded plugin under the given name, so that
     * {@code getPluginManager().getPlugin(name)} finds it, and returns it. The plugin answers
     * {@code getName()} only; it is not an instance of any real plugin class, which is what a
     * test wants when it needs a plugin to be present without standing the real one up.
     */
    public static Plugin registerPlugin(String name) {
        Plugin plugin = BukkitTestDoubles.proxy(Plugin.class, (method, args) -> {
            if (method.getName().equals("getName")) {
                return name;
            }
            throw BukkitTestDoubles.unsupported(method);
        });
        pluginsByName.put(name, plugin);
        return plugin;
    }

    /**
     * Registers an offline player under a freshly generated UUID and returns it.
     * A null {@code name} models an entry whose name the server cannot resolve.
     */
    public static UUID registerOfflinePlayer(String name) {
        UUID uuid = UUID.randomUUID();
        namesByUuid.put(uuid, name);
        return uuid;
    }

    /** Registers an offline player under a UUID the test already holds. */
    public static void registerOfflinePlayer(UUID uuid, String name) {
        namesByUuid.put(uuid, name);
    }

    /**
     * Registers a loaded world under a freshly generated id and returns it. An id that was
     * never registered models a world the server cannot resolve — one that is unloaded or
     * unknown — for which Bukkit answers null.
     */
    public static UUID registerWorld(String name) {
        UUID uuid = UUID.randomUUID();
        worldNamesByUuid.put(uuid, name);
        return uuid;
    }

    private static Server server() {
        return BukkitTestDoubles.proxy(Server.class, (method, args) -> {
            switch (method.getName()) {
                case "getOfflinePlayers":
                    return offlinePlayers();
                case "getOfflinePlayer":
                    if (args != null && args.length == 1 && args[0] instanceof UUID) {
                        UUID uuid = (UUID) args[0];
                        // Bukkit answers an unknown UUID with an OfflinePlayer whose name is
                        // null rather than with null, so the double does the same.
                        return offlinePlayer(uuid, namesByUuid.get(uuid));
                    }
                    throw BukkitTestDoubles.unsupported(method);
                case "getWorld":
                    if (args != null && args.length == 1 && args[0] instanceof UUID) {
                        String worldName = worldNamesByUuid.get(args[0]);
                        return worldName == null ? null : BukkitTestDoubles.world(worldName);
                    }
                    throw BukkitTestDoubles.unsupported(method);
                case "getPluginManager":
                    return pluginManager();
                // Bukkit.setServer() announces the server it was handed, so these four are
                // answered for that call alone.
                case "getLogger":
                    return java.util.logging.Logger.getLogger(FakeBukkitServer.class.getName());
                case "getName":
                    return "Fiefs test double";
                case "getVersion":
                case "getBukkitVersion":
                    return "test";
                default:
                    throw BukkitTestDoubles.unsupported(method);
            }
        });
    }

    /**
     * Answers {@code getPlugin(String)} from the plugin registry — null for a name that was
     * never registered, which is what Bukkit answers for a plugin that is not installed.
     */
    private static PluginManager pluginManager() {
        return BukkitTestDoubles.proxy(PluginManager.class, (method, args) -> {
            if (method.getName().equals("getPlugin") && args != null && args.length == 1
                    && args[0] instanceof String) {
                return pluginsByName.get(args[0]);
            }
            throw BukkitTestDoubles.unsupported(method);
        });
    }

    private static OfflinePlayer[] offlinePlayers() {
        List<OfflinePlayer> players = new ArrayList<>();
        for (Map.Entry<UUID, String> entry : namesByUuid.entrySet()) {
            players.add(offlinePlayer(entry.getKey(), entry.getValue()));
        }
        return players.toArray(new OfflinePlayer[0]);
    }

    private static OfflinePlayer offlinePlayer(UUID uuid, String name) {
        return BukkitTestDoubles.proxy(OfflinePlayer.class, (method, args) -> {
            switch (method.getName()) {
                case "getName":
                    return name;
                case "getUniqueId":
                    return uuid;
                default:
                    throw BukkitTestDoubles.unsupported(method);
            }
        });
    }
}
