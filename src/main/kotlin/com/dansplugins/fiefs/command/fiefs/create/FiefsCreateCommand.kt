package com.dansplugins.fiefs.command.fiefs.create

import com.dansplugins.fiefs.Fiefs
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class FiefsCreateCommand(private val plugin: Fiefs) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}" + "This command can only be run by a player.")
            return false
        }
        val name = args.firstOrNull()
        if (name == null) {
            sender.sendMessage("${ChatColor.RED}" + "Please specify a name for the fief.")
            return false
        }
        if (plugin.fiefRepository.getFief(name) != null) {
            sender.sendMessage("${ChatColor.RED}" + "A fief with that name already exists.")
            return false
        }

        // player must be in a faction
        val mfPlayer = plugin.medievalFactions.services.playerService.getPlayer(sender)
        val faction = plugin.medievalFactions.services.factionService.getFaction(mfPlayer?.id ?: return false)
        if (faction == null) {
            sender.sendMessage("${ChatColor.RED}" + "You must be in a faction to create a fief.")
            return false
        }

        // player must not be in a fief already
        val playersFief = plugin.fiefRepository.getPlayersFief(sender.uniqueId)
        if (playersFief != null) {
            sender.sendMessage("${ChatColor.RED}" + "You're already in a fief. You must leave it first.")
            return false
        }

        val newFief = plugin.fiefFactory.createFief(name, sender.uniqueId)
        plugin.fiefRepository.addFief(newFief)
        sender.sendMessage("${ChatColor.GREEN}" + "Fief created.")
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        return null
    }
}