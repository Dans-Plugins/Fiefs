package dansplugins.fiefs.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * Resolves between player names and UUIDs using Bukkit's offline player cache.
 *
 * @author Daniel McCoy Stephenson
 */
public class UUIDChecker {

    public UUID findUUIDBasedOnPlayerName(String playerName) {
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName() != null && offlinePlayer.getName().equalsIgnoreCase(playerName)) {
                return offlinePlayer.getUniqueId();
            }
        }
        return null;
    }

    public String findPlayerNameBasedOnUUID(UUID playerUUID) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        if (offlinePlayer.getName() != null) {
            return offlinePlayer.getName();
        }
        return playerUUID.toString();
    }
}
