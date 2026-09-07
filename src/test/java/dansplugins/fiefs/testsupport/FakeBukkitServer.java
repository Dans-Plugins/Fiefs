package dansplugins.fiefs.testsupport;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A stand-in for the Bukkit server singleton, holding the offline-player cache that
 * {@code dansplugins.fiefs.utils.UUIDChecker} reads through the static {@link Bukkit} accessors.
 *
 * <p>{@link Bukkit#setServer(Server)} refuses to replace a server once one is set, and Surefire
 * runs the whole suite in one JVM, so the double is installed once per JVM and its registry is
 * cleared between tests. Call {@link #install()} from a {@code @BeforeEach} — it is idempotent —
 * and register whichever players the test needs.
 *
 * <p>Only the methods the code under test reaches are answered; anything else throws, so a test
 * that starts touching a new part of the server surface fails loudly rather than reading a null.
 */
public final class FakeBukkitServer {

    private static final Map<UUID, String> namesByUuid = new LinkedHashMap<>();

    private FakeBukkitServer() {
        // static methods only
    }

    /**
     * Installs the double as the Bukkit server if no server is set yet, and empties the
     * offline-player registry so the calling test starts from a known state.
     */
    public static void install() {
        if (Bukkit.getServer() == null) {
            Bukkit.setServer(server());
        }
        namesByUuid.clear();
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
