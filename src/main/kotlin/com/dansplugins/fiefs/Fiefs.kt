package com.dansplugins.fiefs

import com.dansplugins.factionsystem.MedievalFactions
import com.dansplugins.fiefs.command.fiefs.FiefsCommand
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin

class Fiefs : JavaPlugin() {
    lateinit var medievalFactions: MedievalFactions

    override fun onEnable() {
        saveDefaultConfig()
        config.options().copyDefaults(true)
        config.set("version", description.version)
        saveConfig()

        Metrics(this, 12743)

        val medievalFactions = server.pluginManager.getPlugin("MedievalFactions") as? MedievalFactions
        if (medievalFactions == null) {
            logger.severe("A compatible version of Medieval Factions was not found. Are you using Medieval Factions 5?")
            isEnabled = false
            return
        } else {
            this.medievalFactions = medievalFactions
        }

        getCommand("fiefs")?.setExecutor(FiefsCommand(this))

        logger.info("Fiefs has been enabled.")
    }

    override fun onDisable() {
        logger.info("Fiefs has been disabled.")
    }


}