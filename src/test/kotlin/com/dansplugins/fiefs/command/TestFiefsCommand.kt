package com.dansplugins.fiefs.command

import com.dansplugins.fiefs.Fiefs
import com.dansplugins.fiefs.command.fiefs.FiefsCommand
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
        verify { mockSender.sendMessage(any<String>()); }
    }

    @Test
    fun testFiefsCommandHelpArgument() {
        // prepare
        val mockSender = mockk<CommandSender>() {
            every { sendMessage(any<String>()) } returns Unit
        }
        val mockCommand = mockk<Command>()
        val args = arrayOf("help")
        val label = "fiefs"
        val fiefsCommand = FiefsCommand(mockPlugin)

        // execute
        fiefsCommand.onCommand(mockSender, mockCommand, label, args)

        // verify
        verify { mockSender.sendMessage(any<String>()); }
    }
}