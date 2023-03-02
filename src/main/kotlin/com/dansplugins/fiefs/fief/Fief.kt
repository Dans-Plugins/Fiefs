package com.dansplugins.fiefs.fief

import java.util.UUID

class Fief(private val name: String, private val ownerUUID: UUID) {
    private var uuid = UUID.randomUUID()
    private var members: MutableList<UUID> = mutableListOf()

    fun getId(): UUID {
        return uuid
    }

    fun addMember(uuid: UUID) {
        members.add(uuid)
    }

    fun removeMember(uuid: UUID) {
        members.remove(uuid)
    }

    fun isMember(uuid: UUID): Boolean {
        return members.contains(uuid)
    }

    fun getMembers(): List<UUID> {
        return members
    }

    fun getOwnerUUID(): UUID {
        return ownerUUID
    }

    fun getName(): String {
        return name
    }
}