package dansplugins.fiefs.services;

import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.fiefs.Fiefs;
import dansplugins.fiefs.commands.*;
import dansplugins.fiefs.integrators.MedievalFactionsIntegrator;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocalCommandService {

    public boolean interpretCommand(CommandSender sender, String label, String[] args) {
        if (label.equalsIgnoreCase("fiefs") || label.equalsIgnoreCase("fi")) {

            if (args.length == 0) {
                sender.sendMessage(ChatColor.AQUA + "Fiefs " + Fiefs.getInstance().getVersion());
                sender.sendMessage(ChatColor.AQUA + "Developer: DanTheTechMan");
                sender.sendMessage(ChatColor.AQUA + "Requested by: Laughingspade");
                sender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/dmccoystephenson/Fiefs/wiki");
                return false;
            }

            String secondaryLabel = args[0];
            String[] arguments = getArguments(args);

            if (secondaryLabel.equalsIgnoreCase("help")) {
                if (!checkPermission(sender, "fiefs.help")) { return false; }
                HelpCommand command = new HelpCommand();
                return command.execute(sender, arguments);
            }

            // disallow the usage of most of the commands if Fiefs cannot utilize Medieval Factions
            if (!MedievalFactionsIntegrator.getInstance().isMedievalFactionsAPIAvailable()) {
                sender.sendMessage(ChatColor.RED + "Fiefs cannot utilize Medieval Factions for some reason. It may have to be updated.");
                return false;
            }

            if (secondaryLabel.equalsIgnoreCase("checkclaim")) {
                if (!checkPermission(sender, "fiefs.checkclaim")) { return false; }
                CheckClaimCommand command = new CheckClaimCommand();
                return command.execute(sender);
            }

            // disallow the usage of the most of the commands if the player's faction has disabled fiefs.
            if (sender instanceof Player) {
                Player player = (Player) sender;
                MF_Faction playersFaction = MedievalFactionsIntegrator.getInstance().getAPI().getFaction(player);
                if (playersFaction != null && !((boolean) playersFaction.getFlag("fiefsEnabled"))) {
                    player.sendMessage(ChatColor.RED + "Your faction has disabled fiefs.");
                    return false;
                }
            }

            if (secondaryLabel.equalsIgnoreCase("list")) {
                if (!checkPermission(sender, "fiefs.list")) { return false; }
                ListCommand command = new ListCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("create")) {
                if (!checkPermission(sender, "fiefs.create")) { return false; }
                CreateCommand command = new CreateCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("disband")) {
                if (!checkPermission(sender, "fiefs.disband")) { return false; }
                DisbandCommand command = new DisbandCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("claim")) {
                if (!checkPermission(sender, "fiefs.claim")) { return false; }
                ClaimCommand command = new ClaimCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("unclaim")) {
                if (!checkPermission(sender, "fiefs.unclaim")) { return false; }
                UnclaimCommand command = new UnclaimCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("info")) {
                if (!checkPermission(sender, "fiefs.info")) { return false; }
                InfoCommand command = new InfoCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("invite")) {
                if (!checkPermission(sender, "fiefs.invite")) { return false; }
                InviteCommand command = new InviteCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("join")) {
                if (!checkPermission(sender, "fiefs.join")) { return false; }
                JoinCommand command = new JoinCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("leave")) {
                if (!checkPermission(sender, "fiefs.leave")) { return false; }
                LeaveCommand command = new LeaveCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("members")) {
                if (!checkPermission(sender, "fiefs.members")) { return false; }
                MembersCommand command = new MembersCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("desc")) {
                if (!checkPermission(sender, "fiefs.desc")) { return false; }
                DescCommand command = new DescCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("kick")) {
                if (!checkPermission(sender, "fiefs.kick")) { return false; }
                KickCommand command = new KickCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("transfer")) {
                if (!checkPermission(sender, "fiefs.transfer")) { return false; }
                TransferCommand command = new TransferCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("flags")) {
                if (!checkPermission(sender, "fiefs.flags")) { return false; }
                FlagsCommand command = new FlagsCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("config")) {
                if (!checkPermission(sender, "fiefs.config")) { return false; }
                ConfigCommand command = new ConfigCommand();
                return command.execute(sender, arguments);
            }


            sender.sendMessage(ChatColor.RED + "Fiefs doesn't recognize that command.");
        }
        return false;
    }

    private String[] getArguments(String[] args) {
        String[] toReturn = new String[args.length - 1];

        for (int i = 1; i < args.length; i++) {
            toReturn[i - 1] = args[i];
        }

        return toReturn;
    }

    private boolean checkPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "In order to use this command, you need the following permission: '" + permission + "'");
            return false;
        }
        return true;
    }

}
