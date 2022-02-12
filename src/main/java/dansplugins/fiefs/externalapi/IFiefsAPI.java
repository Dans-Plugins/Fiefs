package dansplugins.fiefs.externalapi;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public interface IFiefsAPI {
    FI_Fief getFief(String fiefName);
    FI_Fief getFief(Player player);
    FI_Fief getFief(UUID playerUUID);
    ArrayList<FI_Fief> getFiefsOfFaction(String factionName);
}