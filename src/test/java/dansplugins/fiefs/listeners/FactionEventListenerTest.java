package dansplugins.fiefs.listeners;

import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.ClaimedChunk;
import dansplugins.fiefs.objects.Fief;
import dansplugins.fiefs.testsupport.BukkitTestDoubles;
import dansplugins.fiefs.testsupport.FakeBukkitServer;
import dansplugins.fiefs.testsupport.MedievalFactionsEvents;
import dansplugins.fiefs.utils.Logger;
import org.bukkit.Chunk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Characterization tests for {@link FactionEventListener}, the path by which changes on the
 * Medieval Factions side reach fief data: a disbanded faction takes its fiefs (and their claims)
 * with it, a player who leaves or is kicked from a faction stops being a fief member, and a
 * chunk a faction unclaims stops being held by a fief. Every one of those handlers deletes
 * persisted state, so the point of the tests below is to pin exactly how much they delete.
 *
 * <p>The events come from {@link MedievalFactionsEvents}, which builds them without a running
 * server or faction service. The listener is
 * given a null {@code MedievalFactions}, which it only dereferences to log a malformed player id
 * in the {@code FactionLeaveEvent} and {@code FactionKickEvent} catch blocks; that pair of
 * branches is therefore the one thing here left unexercised, since the plugin instance behind
 * {@code getLogger()} cannot be stood up outside a server. The integrator is null for the same
 * reason, and none of the lookups these handlers make reach it.
 */
class FactionEventListenerTest {

    private static final Logger NULL_LOGGER = new Logger(null);
    private static final String FACTION = "faction-1";
    private static final String OTHER_FACTION = "faction-2";

    private final PersistentData persistentData = new PersistentData(null);
    private final FactionEventListener listener = new FactionEventListener(persistentData, null);

    @BeforeEach
    void installServer() {
        FakeBukkitServer.install();
    }

    private Fief fief(String name, String factionId, UUID owner) {
        Fief fief = new Fief(null, name, owner, factionId, NULL_LOGGER);
        persistentData.addFief(fief);
        return fief;
    }

    private ClaimedChunk claim(Chunk chunk, String fiefName) {
        ClaimedChunk claimedChunk = new ClaimedChunk(chunk, FACTION, fiefName);
        persistentData.addChunk(claimedChunk);
        return claimedChunk;
    }

    // --- FactionDisbandEvent -------------------------------------------------------------

    @Test
    void factionDisband_removesEveryFiefOfThatFaction() {
        fief("Testopia", FACTION, UUID.randomUUID());
        fief("Secondia", FACTION, UUID.randomUUID());

        listener.handle(MedievalFactionsEvents.factionDisband(FACTION));

        assertEquals(0, persistentData.getFiefs().size());
    }

    @Test
    void factionDisband_leavesTheFiefsOfEveryOtherFaction() {
        fief("Testopia", FACTION, UUID.randomUUID());
        Fief survivor = fief("Elsewhere", OTHER_FACTION, UUID.randomUUID());

        listener.handle(MedievalFactionsEvents.factionDisband(FACTION));

        assertEquals(1, persistentData.getFiefs().size());
        assertSame(survivor, persistentData.getFiefs().get(0));
    }

    @Test
    void factionDisband_alsoReleasesTheLandThoseFiefsHeld() {
        fief("Testopia", FACTION, UUID.randomUUID());
        claim(BukkitTestDoubles.chunk("world", 1, 2), "Testopia");
        claim(BukkitTestDoubles.chunk("world", 3, 4), "Testopia");

        listener.handle(MedievalFactionsEvents.factionDisband(FACTION));

        assertEquals(0, persistentData.getNumChunks());
    }

    @Test
    void factionDisband_leavesTheLandHeldByAnotherFactionsFiefs() {
        fief("Testopia", FACTION, UUID.randomUUID());
        fief("Elsewhere", OTHER_FACTION, UUID.randomUUID());
        claim(BukkitTestDoubles.chunk("world", 1, 2), "Testopia");
        ClaimedChunk survivor = claim(BukkitTestDoubles.chunk("world", 3, 4), "Elsewhere");

        listener.handle(MedievalFactionsEvents.factionDisband(FACTION));

        assertEquals(1, persistentData.getNumChunks());
        assertSame(survivor, persistentData.getClaimedChunks().get(0));
    }

    @Test
    void factionDisband_forAFactionWithNoFiefsChangesNothing() {
        Fief fief = fief("Elsewhere", OTHER_FACTION, UUID.randomUUID());
        ClaimedChunk claimedChunk = claim(BukkitTestDoubles.chunk("world", 1, 2), "Elsewhere");

        listener.handle(MedievalFactionsEvents.factionDisband(FACTION));

        assertEquals(1, persistentData.getFiefs().size());
        assertSame(fief, persistentData.getFiefs().get(0));
        assertEquals(1, persistentData.getNumChunks());
        assertSame(claimedChunk, persistentData.getClaimedChunks().get(0));
    }

    // --- FactionLeaveEvent ---------------------------------------------------------------

    @Test
    void factionLeave_dropsThePlayerFromTheFiefTheyBelongedTo() {
        UUID owner = UUID.randomUUID();
        UUID member = UUID.randomUUID();
        Fief fief = fief("Testopia", FACTION, owner);
        fief.addMember(member);

        listener.handle(MedievalFactionsEvents.factionLeave(FACTION, member.toString()));

        assertFalse(fief.isMember(member));
        assertTrue(fief.isMember(owner));
        assertEquals(1, fief.getNumMembers());
    }

    @Test
    void factionLeave_leavesTheFiefItselfInPlace() {
        UUID owner = UUID.randomUUID();
        UUID member = UUID.randomUUID();
        Fief fief = fief("Testopia", FACTION, owner);
        fief.addMember(member);
        claim(BukkitTestDoubles.chunk("world", 1, 2), "Testopia");

        listener.handle(MedievalFactionsEvents.factionLeave(FACTION, member.toString()));

        assertSame(fief, persistentData.getFief("Testopia"));
        assertEquals(1, persistentData.getNumChunks());
    }

    @Test
    void factionLeave_byTheOwnerLeavesTheFiefRegisteredWithNoMembersAndStillHoldingItsLand() {
        // Unlike /fi leave, which disbands the fief when its owner leaves, this handler only
        // drops the membership: the fief stays in persistent data with zero members, still
        // naming the departed player as its owner, and still holding its claims.
        UUID owner = UUID.randomUUID();
        Fief fief = fief("Testopia", FACTION, owner);
        claim(BukkitTestDoubles.chunk("world", 1, 2), "Testopia");

        listener.handle(MedievalFactionsEvents.factionLeave(FACTION, owner.toString()));

        assertNotNull(persistentData.getFief("Testopia"));
        assertEquals(0, fief.getNumMembers());
        assertEquals(owner, fief.getOwnerUUID());
        assertEquals(1, persistentData.getNumChunks());
    }

    @Test
    void factionLeave_byAPlayerInNoFiefChangesNothing() {
        UUID owner = UUID.randomUUID();
        Fief fief = fief("Testopia", FACTION, owner);

        listener.handle(MedievalFactionsEvents.factionLeave(FACTION, UUID.randomUUID().toString()));

        assertEquals(1, fief.getNumMembers());
        assertTrue(fief.isMember(owner));
    }

    @Test
    void factionLeave_ignoresWhichFactionTheEventNamesAndGoesByThePlayerAlone() {
        // The handler resolves the fief from the player id only, so a leave event naming a
        // faction the player's fief does not belong to still removes them.
        UUID owner = UUID.randomUUID();
        UUID member = UUID.randomUUID();
        Fief fief = fief("Testopia", FACTION, owner);
        fief.addMember(member);

        listener.handle(MedievalFactionsEvents.factionLeave(OTHER_FACTION, member.toString()));

        assertFalse(fief.isMember(member));
    }

    // --- FactionKickEvent ----------------------------------------------------------------

    @Test
    void factionKick_dropsThePlayerFromTheFiefTheyBelongedTo() {
        UUID owner = UUID.randomUUID();
        UUID member = UUID.randomUUID();
        Fief fief = fief("Testopia", FACTION, owner);
        fief.addMember(member);

        listener.handle(MedievalFactionsEvents.factionKick(FACTION, member.toString()));

        assertFalse(fief.isMember(member));
        assertTrue(fief.isMember(owner));
    }

    @Test
    void factionKick_byAPlayerInNoFiefChangesNothing() {
        UUID owner = UUID.randomUUID();
        Fief fief = fief("Testopia", FACTION, owner);

        listener.handle(MedievalFactionsEvents.factionKick(FACTION, UUID.randomUUID().toString()));

        assertEquals(1, fief.getNumMembers());
        assertTrue(fief.isMember(owner));
    }

    @Test
    void factionKick_ofTheOwnerLeavesTheFiefRegisteredWithNoMembers() {
        UUID owner = UUID.randomUUID();
        Fief fief = fief("Testopia", FACTION, owner);

        listener.handle(MedievalFactionsEvents.factionKick(FACTION, owner.toString()));

        assertNotNull(persistentData.getFief("Testopia"));
        assertEquals(0, fief.getNumMembers());
    }

    // --- FactionUnclaimEvent -------------------------------------------------------------

    @Test
    void factionUnclaim_releasesTheFiefClaimAtThoseCoordinates() {
        UUID worldId = FakeBukkitServer.registerWorld("world");
        fief("Testopia", FACTION, UUID.randomUUID());
        claim(BukkitTestDoubles.chunk("world", 1, 2), "Testopia");

        listener.handle(MedievalFactionsEvents.factionUnclaim(FACTION, worldId, 1, 2));

        assertEquals(0, persistentData.getNumChunks());
    }

    @Test
    void factionUnclaim_leavesTheFiefTheChunkBelongedTo() {
        UUID worldId = FakeBukkitServer.registerWorld("world");
        Fief fief = fief("Testopia", FACTION, UUID.randomUUID());
        claim(BukkitTestDoubles.chunk("world", 1, 2), "Testopia");

        listener.handle(MedievalFactionsEvents.factionUnclaim(FACTION, worldId, 1, 2));

        assertSame(fief, persistentData.getFief("Testopia"));
    }

    @Test
    void factionUnclaim_leavesClaimsAtOtherCoordinatesInTheSameWorld() {
        UUID worldId = FakeBukkitServer.registerWorld("world");
        claim(BukkitTestDoubles.chunk("world", 1, 2), "Testopia");
        ClaimedChunk survivor = claim(BukkitTestDoubles.chunk("world", 3, 4), "Testopia");

        listener.handle(MedievalFactionsEvents.factionUnclaim(FACTION, worldId, 1, 2));

        assertEquals(1, persistentData.getNumChunks());
        assertSame(survivor, persistentData.getClaimedChunks().get(0));
    }

    @Test
    void factionUnclaim_leavesTheSameCoordinatesInAnotherWorld() {
        FakeBukkitServer.registerWorld("world");
        UUID netherId = FakeBukkitServer.registerWorld("world_nether");
        ClaimedChunk survivor = claim(BukkitTestDoubles.chunk("world", 1, 2), "Testopia");

        listener.handle(MedievalFactionsEvents.factionUnclaim(FACTION, netherId, 1, 2));

        assertEquals(1, persistentData.getNumChunks());
        assertSame(survivor, persistentData.getClaimedChunks().get(0));
    }

    @Test
    void factionUnclaim_inAWorldTheServerCannotResolveReleasesNothing() {
        // An id that was never registered stands for an unloaded or unknown world, which
        // Bukkit answers with null. The handler must not guess at coordinates alone.
        ClaimedChunk survivor = claim(BukkitTestDoubles.chunk("world", 1, 2), "Testopia");

        listener.handle(MedievalFactionsEvents.factionUnclaim(FACTION, UUID.randomUUID(), 1, 2));

        assertEquals(1, persistentData.getNumChunks());
        assertSame(survivor, persistentData.getClaimedChunks().get(0));
    }

    @Test
    void factionUnclaim_skipsAStoredClaimWhoseChunkNeverResolved() {
        // A claim loaded from disk for a world the server could not create keeps its world
        // name but has no chunk, so reading its coordinates would throw. The stale entry is
        // stepped over and the real claim behind it is still released.
        UUID worldId = FakeBukkitServer.registerWorld("world");
        ClaimedChunk stale = new ClaimedChunk();
        stale.setWorld("world");
        stale.setFief("Testopia");
        persistentData.addChunk(stale);
        claim(BukkitTestDoubles.chunk("world", 1, 2), "Testopia");

        listener.handle(MedievalFactionsEvents.factionUnclaim(FACTION, worldId, 1, 2));

        assertEquals(1, persistentData.getNumChunks());
        assertSame(stale, persistentData.getClaimedChunks().get(0));
    }

    @Test
    void factionUnclaim_releasesOnlyOneClaimEvenIfTwoAreStoredForTheSameChunk() {
        // Nothing stops duplicate entries from accumulating in claimedChunks.json, and the
        // handler stops at the first match, so a duplicate outlives the unclaim.
        UUID worldId = FakeBukkitServer.registerWorld("world");
        claim(BukkitTestDoubles.chunk("world", 1, 2), "Testopia");
        claim(BukkitTestDoubles.chunk("world", 1, 2), "Testopia");

        listener.handle(MedievalFactionsEvents.factionUnclaim(FACTION, worldId, 1, 2));

        assertEquals(1, persistentData.getNumChunks());
    }

    @Test
    void factionUnclaim_withNothingClaimedChangesNothing() {
        UUID worldId = FakeBukkitServer.registerWorld("world");

        listener.handle(MedievalFactionsEvents.factionUnclaim(FACTION, worldId, 1, 2));

        assertEquals(0, persistentData.getNumChunks());
        assertNull(persistentData.getFief("Testopia"));
    }
}
