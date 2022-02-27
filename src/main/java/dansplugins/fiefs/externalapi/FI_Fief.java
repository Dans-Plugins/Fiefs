package dansplugins.fiefs.externalapi;

import java.util.UUID;

import org.bukkit.entity.Player;

import dansplugins.fiefs.objects.Fief;

/**
 * @author Daniel McCoy Stephenson
 */
public class FI_Fief implements IFI_Fief {
    private final Fief fief;

    public FI_Fief(Fief fief) {
        this.fief = fief;
    }

    @Override
    public String getName() {
        return fief.getName();
    }

    @Override
    public UUID getOwner() {
        return fief.getOwnerUUID();
    }

    @Override
    public boolean isMember(Player player) {
        return fief.isMember(player.getUniqueId());
    }

    @Override
    public Object getFlag(String flag) {
        return fief.getFlags().getFlag(flag);
    }
}
