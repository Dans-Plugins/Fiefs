package dansplugins.fiefs.data;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import dansplugins.fiefs.objects.ClaimedChunk;
import dansplugins.fiefs.objects.Fief;
import dansplugins.fiefs.utils.UUIDChecker;

/**
 * @author Daniel McCoy Stephenson
 */
public class PersistentData {
    private static PersistentData instance;
    private ArrayList<Fief> fiefs = new ArrayList<>();
    private ArrayList<ClaimedChunk> claimedChunks = new ArrayList<>();

    private PersistentData() {

    }

    public static PersistentData getInstance() {
        if (instance == null) {
            instance = new PersistentData();
        }
        return instance;
    }

    public ArrayList<Fief> getFiefs() {
        return fiefs;
    }

    public Fief getFief(String name) {
        for (Fief fief : fiefs) {
            if (fief.getName().equalsIgnoreCase(name)) {
                return fief;
            }
        }
        return null;
    }

    public Fief getFief(Player player) {
        for (Fief fief : fiefs) {
            if (fief.isMember(player.getUniqueId())) {
                return fief;
            }
        }
        return null;
    }

    public Fief getFief(UUID playerUUID) {
        for (Fief fief : fiefs) {
            if (fief.isMember(playerUUID)) {
                return fief;
            }
        }
        return null;
    }

    public ArrayList<Fief> getFiefsOfFaction(MF_Faction faction) {
        ArrayList<Fief> toReturn = new ArrayList<>();
        for (Fief fief : fiefs) {
            if (fief.getFactionName().equalsIgnoreCase(faction.getName())) {
                toReturn.add(fief);
            }
        }
        return toReturn;
    }

    public ArrayList<Fief> getFiefsOfFaction(String factionName) {
        ArrayList<Fief> toReturn = new ArrayList<>();
        for (Fief fief : fiefs) {
            if (fief.getFactionName().equalsIgnoreCase(factionName)) {
                toReturn.add(fief);
            }
        }
        return toReturn;
    }

    public boolean isNameTaken(String name) {
        for (Fief fief : fiefs) {
            if (fief.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean addFief(Fief fief) {
        if (isNameTaken(fief.getName())) {
            return false;
        }
        fiefs.add(fief);
        return true;
    }

    public boolean removeFief(Fief fiefToRemove) {
        return fiefs.remove(fiefToRemove);
    }

    public void sendListOfFiefsToPlayer(Player player) {

        MF_Faction faction = MedievalFactionsIntegrator.getInstance().getAPI().getFaction(player);

        if (faction == null) {
            player.sendMessage(ChatColor.RED + "You are not in a faction.");
            return;
        }

        ArrayList<Fief> listOfFiefs = getFiefsOfFaction(faction);

        if (listOfFiefs.size() == 0) {
            player.sendMessage(ChatColor.AQUA + "Your faction doesn't have any fiefs yet.");
            return;
        }

        player.sendMessage(ChatColor.AQUA + "=== Fiefs of " + faction.getName() + " ===");
        player.sendMessage(ChatColor.AQUA + "P: power, M: members, L: land");
        player.sendMessage(ChatColor.AQUA + "-----");
        for (Fief fief : listOfFiefs) {
            player.sendMessage(ChatColor.AQUA + String.format("%-25s %10s %10s %10s", fief.getName(), "P: " +
                    fief.getCumulativePowerLevel(), "M: " + fief.getNumMembers(), "L: " +
                    PersistentData.getInstance().getNumChunksClaimedByFief(fief)));
        }
    }

    public void clearFiefs() {
        fiefs.clear();
    }

    public void clearClaimedChunks() {
        claimedChunks.clear();
    }

    public void addChunk(ClaimedChunk chunk) {
        claimedChunks.add(chunk);
    }

    public void removeChunk(ClaimedChunk chunk) {
        claimedChunks.remove(chunk);
    }

    public int getNumChunks() {
        return claimedChunks.size();
    }

    public ArrayList<ClaimedChunk> getClaimedChunks() {
        return claimedChunks;
    }

    public int getNumChunksClaimedByFief(Fief playersFief) {
        int count = 0;
        for (ClaimedChunk chunk : claimedChunks) {
            if (chunk.getFief().equalsIgnoreCase(playersFief.getName())) {
                count++;
            }
        }
        return count;
    }

    public void sendFiefInfoToPlayer(Player player, Fief playersFief) {
        UUIDChecker uuidChecker = new UUIDChecker();

        int cumulativePowerLevel = playersFief.getCumulativePowerLevel();

        player.sendMessage(ChatColor.AQUA + "=== " + playersFief.getName() + " Fief Info ===");
        player.sendMessage(ChatColor.AQUA + "Name: " + playersFief.getName());
        player.sendMessage(ChatColor.AQUA + "Faction: " + playersFief.getFactionName());
        player.sendMessage(ChatColor.AQUA + "Owner: " + uuidChecker.findPlayerNameBasedOnUUID(playersFief.getOwnerUUID()));
        player.sendMessage(ChatColor.AQUA + "Members: " + playersFief.getNumMembers());
        player.sendMessage(ChatColor.AQUA + "Power Level: " + cumulativePowerLevel);
        player.sendMessage(ChatColor.AQUA + "Demesne Size: " + PersistentData.getInstance().getNumChunksClaimedByFief(playersFief) + "/" + cumulativePowerLevel);
    }
}