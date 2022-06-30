package dansplugins.fiefs.externalapi;

import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.Fief;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public class FiefsAPI {
    private final PersistentData persistentData;

    public FiefsAPI(PersistentData persistentData) {
        this.persistentData = persistentData;
    }

    public FI_Fief getFief(String fiefName) {
        return new FI_Fief(persistentData.getFief(fiefName));
    }

    public FI_Fief getFief(Player player) {
        return new FI_Fief(persistentData.getFief(player));
    }

    public FI_Fief getFief(UUID playerUUID) {
        return new FI_Fief(persistentData.getFief(playerUUID));
    }

    public ArrayList<FI_Fief> getFiefsOfFaction(String factionName) {
        ArrayList<Fief> fiefs = persistentData.getFiefsOfFaction(factionName);
        ArrayList<FI_Fief> toReturn = new ArrayList<>();
        for (Fief fief : fiefs) {
            toReturn.add(new FI_Fief(fief));
        }
        return toReturn;
    }
}
