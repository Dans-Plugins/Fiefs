package dansplugins.fiefs.objects;

import dansplugins.fiefs.utils.Logger;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Characterization tests for {@link Fief}'s in-memory state and the save()/load()
 * round trip. The Medieval Factions integrator and Bukkit types are not exercised
 * here since {@link Fief}'s constructors and the methods under test don't call into them.
 */
class FiefTest {

    private static final Logger NULL_LOGGER = new Logger(null);

    private Fief newFief(UUID owner, String factionId) {
        return new Fief(null, "Test Fief", owner, factionId, NULL_LOGGER);
    }

    @Test
    void constructor_addsOwnerAsMember() {
        UUID owner = UUID.randomUUID();
        Fief fief = newFief(owner, "faction-1");

        assertTrue(fief.isMember(owner));
        assertEquals(1, fief.getNumMembers());
    }

    @Test
    void constructor_initializesDefaultFlags() {
        Fief fief = newFief(UUID.randomUUID(), "faction-1");

        assertEquals(true, fief.getFlags().getBooleanValues().get("claimedLandProtected"));
    }

    @Test
    void addMember_addsNewPlayer() {
        Fief fief = newFief(UUID.randomUUID(), "faction-1");
        UUID member = UUID.randomUUID();

        fief.addMember(member);

        assertTrue(fief.isMember(member));
        assertEquals(2, fief.getNumMembers());
    }

    @Test
    void addMember_doesNotDuplicateExistingMember() {
        Fief fief = newFief(UUID.randomUUID(), "faction-1");
        UUID member = UUID.randomUUID();

        fief.addMember(member);
        fief.addMember(member);

        assertEquals(2, fief.getNumMembers());
    }

    @Test
    void removeMember_removesExistingMember() {
        Fief fief = newFief(UUID.randomUUID(), "faction-1");
        UUID member = UUID.randomUUID();
        fief.addMember(member);

        fief.removeMember(member);

        assertFalse(fief.isMember(member));
    }

    @Test
    void removeMember_isNoOpForNonMember() {
        Fief fief = newFief(UUID.randomUUID(), "faction-1");
        int before = fief.getNumMembers();

        fief.removeMember(UUID.randomUUID());

        assertEquals(before, fief.getNumMembers());
    }

    @Test
    void getMembers_returnsUnmodifiableSnapshot() {
        Fief fief = newFief(UUID.randomUUID(), "faction-1");
        List<UUID> members = fief.getMembers();

        assertThrows(UnsupportedOperationException.class, () -> members.add(UUID.randomUUID()));
    }

    @Test
    void invitePlayer_addsInvite() {
        Fief fief = newFief(UUID.randomUUID(), "faction-1");
        UUID invitee = UUID.randomUUID();

        fief.invitePlayer(invitee);

        assertTrue(fief.isInvited(invitee));
    }

    @Test
    void invitePlayer_doesNotDuplicateExistingInvite() {
        Fief fief = newFief(UUID.randomUUID(), "faction-1");
        UUID invitee = UUID.randomUUID();

        fief.invitePlayer(invitee);
        fief.invitePlayer(invitee);

        assertTrue(fief.isInvited(invitee));
    }

    @Test
    void uninvitePlayer_removesInvite() {
        Fief fief = newFief(UUID.randomUUID(), "faction-1");
        UUID invitee = UUID.randomUUID();
        fief.invitePlayer(invitee);

        fief.uninvitePlayer(invitee);

        assertFalse(fief.isInvited(invitee));
    }

    @Test
    void equals_trueForSameOwnerNameAndFaction() {
        UUID owner = UUID.randomUUID();
        Fief a = newFief(owner, "faction-1");
        Fief b = newFief(owner, "faction-1");

        assertTrue(a.equals(b));
    }

    @Test
    void equals_falseForDifferentFaction() {
        UUID owner = UUID.randomUUID();
        Fief a = newFief(owner, "faction-1");
        Fief b = newFief(owner, "faction-2");

        assertFalse(a.equals(b));
    }

    /**
     * Pins a known bug (Dans-Plugins/Fiefs#150): the load-from-storage constructor calls
     * load(fiefData) before assigning `flags`, so load() NPEs on `flags.setIntegerValues(...)`.
     * This is the exact constructor StorageService.loadFiefs() uses on every plugin startup,
     * so any server restart with saved fief data currently fails to load it.
     * Once #150 is fixed, replace this with a real save()/load() round-trip assertion.
     */
    @Test
    void loadingFromSaveData_currentlyThrowsNullPointerException_knownBugIssue150() {
        UUID owner = UUID.randomUUID();
        Fief original = newFief(owner, "faction-1");
        original.setDescription("A cozy fief");

        Map<String, String> saved = original.save();

        assertThrows(NullPointerException.class, () -> new Fief(saved, null, NULL_LOGGER));
    }
}
