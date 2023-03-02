package com.dansplugins.fiefs.fief

import com.dansplugins.fiefs.Fiefs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// import mockk
import io.mockk.every
import io.mockk.mockk
import java.util.*

class TestFiefFactory {

    @Test
    fun testCreateFief() {
        val plugin = mockk<Fiefs>()
        val fiefFactory = FiefFactory(plugin)
        val uuid = UUID.randomUUID()
        val fief = fiefFactory.createFief("testFief", uuid)
        assertEquals("testFief", fief.getName())
        assertEquals(uuid, fief.getOwnerUUID())
        assertEquals(0, fief.getMembers().size)
    }
}