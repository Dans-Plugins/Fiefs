package com.dansplugins.fiefs.command.fiefs

import com.dansplugins.fiefs.Fiefs
import com.dansplugins.fiefs.command.fiefs.create.FiefsCreateCommand
import com.dansplugins.fiefs.command.fiefs.help.FiefsHelpCommand
import com.dansplugins.fiefs.command.fiefs.list.FiefsListCommand
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class FiefsCommand(private val plugin: Fiefs) : CommandExecutor, TabCompleter {
    private val helpCommand = FiefsHelpCommand(plugin)
    private val createCommand = FiefsCreateCommand(plugin)
    private val listCommand = FiefsListCommand(plugin)

    private val subcommands = listOf(
        "help",
        "create",
        "list"
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return when (args.firstOrNull()?.lowercase()) {
            "help" -> helpCommand.onCommand(sender, command, label, args.drop(1).toTypedArray())
            "create" -> createCommand.onCommand(sender, command, label, args.drop(1).toTypedArray())
            "list" -> listCommand.onCommand(sender, command, label, args.drop(1).toTypedArray())
            else -> {
                // send plugin information
                sender.sendMessage("${ChatColor.AQUA}Fiefs v${plugin.description.version}")
                sender.sendMessage("${ChatColor.AQUA}Author: ${plugin.description.authors}")
                sender.sendMessage("${ChatColor.AQUA}Description: ${plugin.description.description}")

                // send help message
                sender.sendMessage("${ChatColor.GREEN}Type /fiefs help for a list of commands.")
                return true
            }
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        return when (args.size) {
            1 -> subcommands.filter { it.startsWith(args[0]) }.toMutableList()
            else -> when(args.first().lowercase()) {
                "help" -> helpCommand.onTabComplete(sender, command, alias, args.drop(1).toTypedArray())
                "create" -> createCommand.onTabComplete(sender, command, alias, args.drop(1).toTypedArray())
                "list" -> listCommand.onTabComplete(sender, command, alias, args.drop(1).toTypedArray())
                else -> emptyList()
            }
        }
    }

}