package dansplugins.fiefs.objects;

import dansplugins.fiefs.testsupport.BukkitTestDoubles;
import org.bukkit.Chunk;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Characterization tests for {@link ClaimedChunk}, covering the on-disk shape of
 * {@code claimedChunks.json}. {@code load(...)} is not exercised here: it calls
 * {@code Bukkit.getServer().createWorld(...)}, which needs a running server.
 */
class ClaimedChunkTest {

    @Test
    void constructor_takesTheWorldNameFromTheChunk() {
        ClaimedChunk claimedChunk = new ClaimedChunk(BukkitTestDoubles.chunk("world", 1, 2), "faction-1", "Testopia");

        assertEquals("world", claimedChunk.getWorld());
    }

    @Test
    void constructor_storesTheChunkFactionAndFiefAsGiven() {
        Chunk chunk = BukkitTestDoubles.chunk("world", 1, 2);

        ClaimedChunk claimedChunk = new ClaimedChunk(chunk, "faction-1", "Testopia");

        assertSame(chunk, claimedChunk.getChunk());
        assertEquals("faction-1", claimedChunk.getFaction());
        assertEquals("Testopia", claimedChunk.getFief());
    }

    @Test
    void getCoordinates_returnsTheChunkXAndZInThatOrder() {
        ClaimedChunk claimedChunk = new ClaimedChunk(BukkitTestDoubles.chunk("world", 1, 2), "faction-1", "Testopia");

        assertArrayEquals(new double[]{1, 2}, claimedChunk.getCoordinates());
    }

    @Test
    void save_writesEveryFieldOfTheOnDiskFormatAsJson() {
        ClaimedChunk claimedChunk = new ClaimedChunk(BukkitTestDoubles.chunk("world", 1, -2), "faction-1", "Testopia");

        Map<String, String> saved = claimedChunk.save();

        // These five keys and their JSON encoding are the claimedChunks.json format that
        // StorageService writes and ClaimedChunk(Map) reads back; changing either without the
        // other silently orphans every chunk already claimed on a live server.
        assertEquals(5, saved.size());
        assertEquals("1", saved.get("X"));
        assertEquals("-2", saved.get("Z"));
        assertEquals("\"world\"", saved.get("world"));
        assertEquals("\"faction-1\"", saved.get("faction"));
        assertEquals("\"Testopia\"", saved.get("fief"));
    }

    @Test
    void save_writesTheStoredWorldNameRatherThanTheCurrentChunkWorld() {
        ClaimedChunk claimedChunk = new ClaimedChunk(BukkitTestDoubles.chunk("world", 1, 2), "faction-1", "Testopia");

        // setChunk(...) leaves the world field alone: only the constructor and setWorld(...)
        // ever write it, so a chunk swapped in from another world saves under the old name.
        claimedChunk.setChunk(BukkitTestDoubles.chunk("world_nether", 3, 4));

        Map<String, String> saved = claimedChunk.save();

        assertEquals("3", saved.get("X"));
        assertEquals("4", saved.get("Z"));
        assertEquals("\"world\"", saved.get("world"));
        assertEquals("world", claimedChunk.getWorld());
    }

    @Test
    void setWorld_replacesTheStoredWorldName() {
        ClaimedChunk claimedChunk = new ClaimedChunk(BukkitTestDoubles.chunk("world", 1, 2), "faction-1", "Testopia");

        claimedChunk.setWorld("world_nether");

        assertEquals("world_nether", claimedChunk.getWorld());
        assertEquals("\"world_nether\"", claimedChunk.save().get("world"));
    }

    @Test
    void setFief_replacesTheOwningFief() {
        ClaimedChunk claimedChunk = new ClaimedChunk(BukkitTestDoubles.chunk("world", 1, 2), "faction-1", "Testopia");

        claimedChunk.setFief("OtherFief");

        assertEquals("OtherFief", claimedChunk.getFief());
    }

    @Test
    void setFaction_replacesTheOwningFaction() {
        ClaimedChunk claimedChunk = new ClaimedChunk(BukkitTestDoubles.chunk("world", 1, 2), "faction-1", "Testopia");

        claimedChunk.setFaction("faction-2");

        assertEquals("faction-2", claimedChunk.getFaction());
    }
}
