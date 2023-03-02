package com.dansplugins.fiefs.fief

import com.dansplugins.fiefs.Fiefs

/**
 * Stores all Fiefs in a list and provides methods for accessing and modifying the list.
 */
class FiefRepository(private val plugin: Fiefs) {
    private val fiefs = mutableListOf<Fief>()

    /**
     * Adds a Fief to the list.
     * @param fief The Fief to add.
     */
    fun addFief(fief: Fief) {
        fiefs.add(fief)
    }

    /**
     * Removes a Fief from the list.
     * @param fief The Fief to remove.
     */
    fun removeFief(fief: Fief) {
        fiefs.remove(fief)
    }

    /**
     * Gets a Fief from the list.
     * @param name The name of the Fief to get.
     * @return The Fief with the given name.
     */
    fun getFief(name: String): Fief? {
        for (fief in fiefs) {
            if (fief.getName() == name) {
                return fief
            }
        }
        return null
    }

    /**
     * Gets a list of all Fiefs.
     * @return A list of all Fiefs.
     */
    fun getFiefs(): List<Fief> {
        return fiefs
    }
}