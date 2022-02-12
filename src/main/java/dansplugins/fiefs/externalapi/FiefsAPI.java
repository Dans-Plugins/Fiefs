package dansplugins.fiefs.externalapi;

import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.Fief;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public class FiefsAPI implements IFiefsAPI {

    @Override
    public FI_Fief getFief(String fiefName) {
        return new FI_Fief(PersistentData.getInstance().getFief(fiefName));
    }

    @Override
    public FI_Fief getFief(Player player) {
        return new FI_Fief(PersistentData.getInstance().getFief(player));
    }

    @Override
    public FI_Fief getFief(UUID playerUUID) {
        return new FI_Fief(PersistentData.getInstance().getFief(playerUUID));
    }

    @Override
    public ArrayList<FI_Fief> getFiefsOfFaction(String factionName) {
        ArrayList<Fief> fiefs = PersistentData.getInstance().getFiefsOfFaction(factionName);
        ArrayList<FI_Fief> toReturn = new ArrayList<>();
        for (Fief fief : fiefs) {
            toReturn.add(new FI_Fief(fief));
        }
        return toReturn;
    }
}
