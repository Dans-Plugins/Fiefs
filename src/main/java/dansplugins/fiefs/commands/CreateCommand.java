package dansplugins.fiefs.commands;

import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.fiefs.MedievalFactionsIntegrator;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.objects.Fief;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand {

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        MF_Faction faction = MedievalFactionsIntegrator.getInstance().getAPI().getFaction(player);
        if (faction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to use this command.");
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /fiefs create (name)");
            return false;
        }

        if (PersistentData.getInstance().getFief(player) != null) {
            player.sendMessage(ChatColor.RED + "You can't create a fief if you're already in a fief.");
            return false;
        }

        String name = args[0]; // TODO: allow for spaces

        if (PersistentData.getInstance().isNameTaken(name)) {
            player.sendMessage(ChatColor.RED + "That name is taken.");
            return false;
        }

        Fief fief = new Fief(name, player.getUniqueId(), faction.getName());
        PersistentData.getInstance().addFief(fief);
        player.sendMessage(ChatColor.GREEN + "Fief created.");
        return true;
    }

}
