package dansplugins.fiefs.externalapi;

import dansplugins.fiefs.objects.Fief;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public class FI_Fief {
    private final Fief fief;

    public FI_Fief(Fief fief) {
        this.fief = fief;
    }

    public String getName() {
        return fief.getName();
    }

    public UUID getOwner() {
        return fief.getOwnerUUID();
    }

    public boolean isMember(Player player) {
        return fief.isMember(player.getUniqueId());
    }

    public Object getFlag(String flag) {
        return fief.getFlags().getFlag(flag);
    }
}
