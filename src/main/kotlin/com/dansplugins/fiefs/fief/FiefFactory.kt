package com.dansplugins.fiefs.fief

import com.dansplugins.fiefs.Fiefs
import com.dansplugins.fiefs.fief.Fief
import java.util.UUID

/**
 * Creates instances of the Fief class.
 */
class FiefFactory(private val plugin: Fiefs) {
    fun createFief(name: String, ownerUUID: UUID): Fief {
        return Fief(name, ownerUUID)
    }
}