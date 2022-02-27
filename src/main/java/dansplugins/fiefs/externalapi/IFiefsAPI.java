package dansplugins.fiefs.externalapi;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * @author Daniel McCoy Stephenson
 */
public interface IFiefsAPI {
    FI_Fief getFief(String fiefName);
    FI_Fief getFief(Player player);
    FI_Fief getFief(UUID playerUUID);
    ArrayList<FI_Fief> getFiefsOfFaction(String factionName);
}