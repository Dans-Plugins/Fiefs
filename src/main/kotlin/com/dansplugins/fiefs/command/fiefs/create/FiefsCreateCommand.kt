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
            sender.sendMessage("This command can only be run by a player.")
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
        val fief = plugin.fiefFactory.createFief(name, sender.uniqueId)
        plugin.fiefRepository.addFief(fief)
        sender.sendMessage("${ChatColor.RED}" + "Fief created.")
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