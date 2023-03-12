package com.dansplugins.fiefs.fief

import com.dansplugins.factionsystem.player.MfPlayerId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class TestFief {

    @Test
    fun testInitialization() {
        val mfPlayerId = MfPlayerId("test")
        val fief = Fief("testFief", mfPlayerId)
        assertEquals("testFief", fief.getName())
        assertEquals(mfPlayerId, fief.getOwnerId())
        assertEquals(0, fief.getMembers().size)
        assertEquals(true, fief.isMember(mfPlayerId))
    }

    @Test
    fun testAddMember() {
        val mfPlayerId = MfPlayerId("test")
        val fief = Fief("testFief", mfPlayerId)
        fief.addMember(mfPlayerId)
        assertEquals(fief.getMembers().size, 1)
        assertEquals(mfPlayerId, fief.getMembers()[0])
    }

    @Test
    fun testRemoveMember() {
        val mfPlayerId = MfPlayerId("test")
        val fief = Fief("testFief", mfPlayerId)
        val memberUUID = UUID.randomUUID()
        fief.addMember(mfPlayerId)
        fief.removeMember(mfPlayerId)
        assertEquals(0, fief.getMembers().size)
    }

    @Test
    fun testIsMember() {
        val mfPlayerId = MfPlayerId("test")
        val fief = Fief("testFief", mfPlayerId)
        val memberUUID = UUID.randomUUID()
        fief.addMember(mfPlayerId)
        assertEquals(true, fief.isMember(mfPlayerId))
    }
}