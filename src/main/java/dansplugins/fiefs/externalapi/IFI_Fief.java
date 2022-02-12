package dansplugins.fiefs.externalapi;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public interface IFI_Fief {
    String getName();
    UUID getOwner();
    boolean isMember(Player player);
    Object getFlag(String flag);
}
