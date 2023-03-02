package com.dansplugins.fiefs.fief

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class TestFief {

    @Test
    fun testInitialization() {
        val uuid = UUID.randomUUID()
        val fief = Fief("testFief", uuid)
        assertEquals("testFief", fief.getName())
        assertEquals(uuid, fief.getOwnerUUID())
        assertEquals(0, fief.getMembers().size)
        assertEquals(fief.isMember(uuid), true)
    }

    @Test
    fun testAddMember() {
        val uuid = UUID.randomUUID()
        val fief = Fief("testFief", uuid)
        val memberUUID = UUID.randomUUID()
        fief.addMember(memberUUID)
        assertEquals(1, fief.getMembers().size)
        assertEquals(memberUUID, fief.getMembers()[0])
    }

    @Test
    fun testRemoveMember() {
        val uuid = UUID.randomUUID()
        val fief = Fief("testFief", uuid)
        val memberUUID = UUID.randomUUID()
        fief.addMember(memberUUID)
        fief.removeMember(memberUUID)
        assertEquals(0, fief.getMembers().size)
    }

    @Test
    fun testIsMember() {
        val uuid = UUID.randomUUID()
        val fief = Fief("testFief", uuid)
        val memberUUID = UUID.randomUUID()
        fief.addMember(memberUUID)
        assertEquals(true, fief.isMember(memberUUID))
    }
}