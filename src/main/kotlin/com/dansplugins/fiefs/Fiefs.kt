package com.dansplugins.fiefs

import com.dansplugins.fiefs.command.fiefs.FiefsCommand
import org.bukkit.plugin.java.JavaPlugin

class Fiefs : JavaPlugin() {
    override fun onEnable() {
        saveDefaultConfig()
        config.options().copyDefaults(true)
        config.set("version", description.version)
        saveConfig()

        getCommand("fiefs")?.setExecutor(FiefsCommand(this))

        logger.info("Fiefs has been enabled.")
    }

    override fun onDisable() {
        logger.info("Fiefs has been disabled.")
    }


}