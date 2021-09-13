package dansplugins.fiefs.externalapi;

import dansplugins.fiefs.objects.Fief;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FI_Fief implements IFI_Fief {

    private Fief fief;

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
