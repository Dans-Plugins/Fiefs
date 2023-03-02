package com.dansplugins.fiefs.command.fiefs.list

import com.dansplugins.fiefs.Fiefs
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class FiefsListCommand(private val plugin: Fiefs) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage("=== Fiefs ===")
        for (fief in plugin.fiefRepository.getFiefs()) {
            sender.sendMessage(fief.getName())
        }
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