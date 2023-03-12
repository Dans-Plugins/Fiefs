package com.dansplugins.fiefs.fief

import com.dansplugins.factionsystem.player.MfPlayerId
import com.dansplugins.fiefs.Fiefs
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class TestFiefRepository {

    @Test
    fun testInitialization() {
        val mockPlugin = mockk<Fiefs>()
        val fiefRepository = FiefRepository(mockPlugin)
        assertEquals(0, fiefRepository.getFiefs().size)
    }

    @Test
    fun testAddFief() {
        val mockPlugin = mockk<Fiefs>()
        val fiefRepository = FiefRepository(mockPlugin)
        val fief = Fief("testFief", MfPlayerId("test"))
        fiefRepository.addFief(fief)
        assertEquals(1, fiefRepository.getFiefs().size)
        assertEquals(fief, fiefRepository.getFiefs()[0])
    }

    @Test
    fun testRemoveFief() {
        val mockPlugin = mockk<Fiefs>()
        val fiefRepository = FiefRepository(mockPlugin)
        val fief = Fief("testFief", MfPlayerId("test"))
        fiefRepository.addFief(fief)
        fiefRepository.removeFief(fief)
        assertEquals(0, fiefRepository.getFiefs().size)
    }

    @Test
    fun testGetFiefByName() {
        val mockPlugin = mockk<Fiefs>()
        val fiefRepository = FiefRepository(mockPlugin)
        val fief = Fief("testFief", MfPlayerId("test"))
        fiefRepository.addFief(fief)
        assertEquals(fief, fiefRepository.getFief("testFief"))
    }

    @Test
    fun testGetFiefByUUID() {
        val mockPlugin = mockk<Fiefs>()
        val fiefRepository = FiefRepository(mockPlugin)
        val uuid = UUID.randomUUID()
        val fief = Fief("testFief", MfPlayerId("test"))
        val fiefId = fief.getId()
        fiefRepository.addFief(fief)
        assertEquals(fief, fiefRepository.getFief(fiefId))
    }

    @Test
    fun testGetFiefByPlayerUUID() {
        val mockPlugin = mockk<Fiefs>()
        val fiefRepository = FiefRepository(mockPlugin)
        val fief = Fief("testFief", MfPlayerId("test"))
        val playerId = MfPlayerId("test")
        fief.addMember(playerId)
        fiefRepository.addFief(fief)
        assertEquals(fief, fiefRepository.getPlayersFief(playerId))
    }
}