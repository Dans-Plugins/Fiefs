package com.dansplugins.fiefs.fief

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
        val fief = Fief("testFief", UUID.randomUUID())
        fiefRepository.addFief(fief)
        assertEquals(1, fiefRepository.getFiefs().size)
        assertEquals(fief, fiefRepository.getFiefs()[0])
    }

    @Test
    fun testRemoveFief() {
        val mockPlugin = mockk<Fiefs>()
        val fiefRepository = FiefRepository(mockPlugin)
        val fief = Fief("testFief", UUID.randomUUID())
        fiefRepository.addFief(fief)
        fiefRepository.removeFief(fief)
        assertEquals(0, fiefRepository.getFiefs().size)
    }

    @Test
    fun testGetFiefByName() {
        val mockPlugin = mockk<Fiefs>()
        val fiefRepository = FiefRepository(mockPlugin)
        val fief = Fief("testFief", UUID.randomUUID())
        fiefRepository.addFief(fief)
        assertEquals(fief, fiefRepository.getFief("testFief"))
    }

    @Test
    fun testGetFiefByUUID() {
        val mockPlugin = mockk<Fiefs>()
        val fiefRepository = FiefRepository(mockPlugin)
        val uuid = UUID.randomUUID()
        val fief = Fief("testFief", uuid)
        fiefRepository.addFief(fief)
        assertEquals(fief, fiefRepository.getFief(uuid))
    }
}