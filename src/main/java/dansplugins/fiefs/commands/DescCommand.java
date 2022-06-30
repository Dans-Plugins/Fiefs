package dansplugins.fiefs.commands;

import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.fiefs.data.PersistentData;
import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import dansplugins.fiefs.objects.Fief;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.misc.ArgumentParser;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class DescCommand extends AbstractPluginCommand {
    private final MedievalFactionsIntegrator medievalFactionsIntegrator;
    private final PersistentData persistentData;

    public DescCommand(MedievalFactionsIntegrator medievalFactionsIntegrator, PersistentData persistentData) {
        super(new ArrayList<>(Arrays.asList("desc")), new ArrayList<>(Arrays.asList("fiefs.desc")));
        this.medievalFactionsIntegrator = medievalFactionsIntegrator;
        this.persistentData = persistentData;
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

        MF_Faction faction = medievalFactionsIntegrator.getAPI().getFaction(player);
        if (faction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to use this command.");
            return false;
        }

        Fief playersFief = persistentData.getFief(player);
        if (playersFief == null) {
            player.sendMessage(ChatColor.RED + "You must be in a fief to use this command.");
            return false;
        }

        ArgumentParser argumentParser = new ArgumentParser();
        ArrayList<String> singleQuoteArgs = argumentParser.getArgumentsInsideDoubleQuotes(args);

        if (singleQuoteArgs.size() == 0) {
            player.sendMessage(ChatColor.RED + "New description must be between double quotes.");
            return false;
        }

        String newDescription = singleQuoteArgs.get(0);

        playersFief.setDescription(newDescription);
        return true;
    }

}