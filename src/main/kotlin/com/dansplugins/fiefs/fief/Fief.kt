package com.dansplugins.fiefs.fief

import com.dansplugins.factionsystem.faction.MfFactionId
import com.dansplugins.factionsystem.player.MfPlayerId
import java.util.UUID

data class Fief(private val name: String, private val ownerMfPlayerId: MfPlayerId, private val mfFactionid: MfFactionId) {
    private var id = MfFiefId.generate()
    private var members: MutableList<MfPlayerId> = mutableListOf()

    fun getId(): MfFiefId {
        return id
    }

    fun addMember(mfPlayerId: MfPlayerId) {
        members.add(mfPlayerId)
    }

    fun removeMember(mfPlayerId: MfPlayerId) {
        members.remove(mfPlayerId)
    }

    fun isMember(mfPlayerId: MfPlayerId): Boolean {
        return members.contains(mfPlayerId) || ownerMfPlayerId == mfPlayerId
    }

    fun getMembers(): List<MfPlayerId> {
        return members
    }

    fun getOwnerId(): MfPlayerId {
        return ownerMfPlayerId
    }

    fun getName(): String {
        return name
    }
}