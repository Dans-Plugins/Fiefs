package com.dansplugins.fiefs.command

import com.dansplugins.fiefs.Fiefs
import com.dansplugins.fiefs.command.fiefs.FiefsCommand
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.junit.jupiter.api.Test

class TestFiefsCommand {

    private val mockPlugin = mockk<Fiefs>() {
        every { description.version } returns "1.0.0"
        every { description.authors } returns listOf("Dan")
        every { description.description } returns "A plugin about fiefs."
    }

    @Test
    fun testFiefsCommandNoArguments() {
        // prepare
        val mockSender = mockk<CommandSender>() {
            every { sendMessage(any<String>()) } returns Unit
        }
        val mockCommand = mockk<Command>()
        val args = emptyArray<String>()
        val label = "fiefs"
        val fiefsCommand = FiefsCommand(mockPlugin)

        // execute
        fiefsCommand.onCommand(mockSender, mockCommand, label, args)

        // verify
        val expectedAuthors = mockPlugin.description.authors
        val expectedDescription = mockPlugin.description.description
        verify(exactly = 1) { mockSender.sendMessage("${ChatColor.AQUA}Fiefs v1.0.0") }
        verify(exactly = 1) { mockSender.sendMessage("${ChatColor.AQUA}Author: $expectedAuthors") }
        verify(exactly = 1) { mockSender.sendMessage("${ChatColor.AQUA}Description: $expectedDescription") }
        verify(exactly = 1) { mockSender.sendMessage("${ChatColor.GREEN}Type /fiefs help for a list of commands.") }
    }
}