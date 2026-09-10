package dansplugins.fiefs.testsupport;

import com.dansplugins.factionsystem.claim.MfClaimedChunk;
import com.dansplugins.factionsystem.event.faction.FactionDisbandEvent;
import com.dansplugins.factionsystem.event.faction.FactionKickEvent;
import com.dansplugins.factionsystem.event.faction.FactionLeaveEvent;
import com.dansplugins.factionsystem.event.faction.FactionUnclaimEvent;

import java.lang.reflect.Constructor;
import java.util.UUID;

/**
 * Builds the Medieval Factions faction events that {@code FactionEventListener} handles.
 *
 * <p>The events themselves are ordinary Bukkit events carrying ids and a claim — nothing in them
 * needs a running server — but every one of their constructors is private in Medieval Factions'
 * Kotlin API, since only Medieval Factions is meant to fire them. Reflection is therefore the
 * only way to hand the listener an event to handle, and it is confined to this class so no test
 * has to repeat it.
 *
 * <p>A signature change upstream fails here with the constructor it looked for and the ones the
 * class actually declares, rather than as an unexplained failure inside a test.
 */
public final class MedievalFactionsEvents {

    private MedievalFactionsEvents() {
        // static factory methods only
    }

    /** A disband event for the faction with the given id. */
    public static FactionDisbandEvent factionDisband(String factionId) {
        return construct(FactionDisbandEvent.class,
                new Class<?>[]{String.class, boolean.class},
                factionId, false);
    }

    /** An event announcing that the given player left the given faction of their own accord. */
    public static FactionLeaveEvent factionLeave(String factionId, String playerId) {
        return construct(FactionLeaveEvent.class,
                new Class<?>[]{String.class, String.class, boolean.class},
                factionId, playerId, false);
    }

    /** An event announcing that the given player was kicked from the given faction. */
    public static FactionKickEvent factionKick(String factionId, String playerId) {
        return construct(FactionKickEvent.class,
                new Class<?>[]{String.class, String.class, boolean.class},
                factionId, playerId, false);
    }

    /** An event announcing that the given faction unclaimed the chunk at those coordinates. */
    public static FactionUnclaimEvent factionUnclaim(String factionId, UUID worldId, int chunkX, int chunkZ) {
        MfClaimedChunk claim = construct(MfClaimedChunk.class,
                new Class<?>[]{UUID.class, int.class, int.class, String.class},
                worldId, chunkX, chunkZ, factionId);
        return construct(FactionUnclaimEvent.class,
                new Class<?>[]{String.class, MfClaimedChunk.class, boolean.class},
                factionId, claim, false);
    }

    private static <T> T construct(Class<T> type, Class<?>[] parameterTypes, Object... arguments) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(arguments);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not build a " + type.getSimpleName()
                    + " for the Medieval Factions version on the test classpath. Declared"
                    + " constructors: " + describeConstructors(type), e);
        }
    }

    private static String describeConstructors(Class<?> type) {
        StringBuilder description = new StringBuilder();
        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            if (description.length() > 0) {
                description.append("; ");
            }
            description.append(constructor);
        }
        return description.toString();
    }
}
