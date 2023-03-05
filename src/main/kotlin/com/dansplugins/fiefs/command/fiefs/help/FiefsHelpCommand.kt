package com.dansplugins.fiefs.command.fiefs.help

import com.dansplugins.fiefs.Fiefs
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class FiefsHelpCommand(private val plugin: Fiefs) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // send list of commands
        sender.sendMessage("${ChatColor.AQUA}=== Fiefs Commands ===")
        sender.sendMessage("${ChatColor.AQUA}/fiefs help - Displays a list of useful commands.")
        sender.sendMessage("${ChatColor.AQUA}/fiefs create <name> - Creates a fief.")
        sender.sendMessage("${ChatColor.AQUA}/fiefs list - Lists all fiefs.")
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        return emptyList()
    }

}