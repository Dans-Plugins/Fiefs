package dansplugins.fiefs.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import dansplugins.fiefs.objects.Fief;
import dansplugins.fiefs.utils.ArgumentParser;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

/**
 * @author Daniel McCoy Stephenson
 */
public class DescCommand extends AbstractPluginCommand {

    public DescCommand() {
        super(new ArrayList<>(Arrays.asList("desc")), new ArrayList<>(Arrays.asList("fiefs.desc")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.RED + "Usage: /fiefs desc 'new description'");
        return false;
    }

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

        Fief playersFief = PersistentData.getInstance().getFief(player);
        if (playersFief == null) {
            player.sendMessage(ChatColor.RED + "You must be in a fief to use this command.");
            return false;
        }

        ArrayList<String> singleQuoteArgs = ArgumentParser.getInstance().getArgumentsInsideSingleQuotes(args);

        if (singleQuoteArgs.size() == 0) {
            player.sendMessage(ChatColor.RED + "New description must be between single quotes.");
            return false;
        }

        String newDescription = singleQuoteArgs.get(0);

        playersFief.setDescription(newDescription);
        return true;
    }

}