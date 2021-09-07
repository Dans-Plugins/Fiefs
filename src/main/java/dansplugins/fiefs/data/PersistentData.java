package dansplugins.fiefs.data;

import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.fiefs.MedievalFactionsIntegrator;
import dansplugins.fiefs.objects.Fief;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PersistentData {

    private static PersistentData instance;

    private ArrayList<Fief> fiefs = new ArrayList<>();

    private PersistentData() {

    }

    public static PersistentData getInstance() {
        if (instance == null) {
            instance = new PersistentData();
        }
        return instance;
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
            if (fief.getOwnerUUID().equals(player.getUniqueId())) {
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
        for (Fief fief : listOfFiefs) {
            player.sendMessage(ChatColor.AQUA + fief.getName());
        }
    }
}