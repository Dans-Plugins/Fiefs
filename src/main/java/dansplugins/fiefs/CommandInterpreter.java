package dansplugins.fiefs;

import dansplugins.fiefs.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandInterpreter {

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
                return command.execute(sender);
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

            if (secondaryLabel.equalsIgnoreCase("checkclaim")) {
                if (!checkPermission(sender, "fiefs.checkclaim")) { return false; }
                CheckClaimCommand command = new CheckClaimCommand();
                return command.execute(sender);
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
