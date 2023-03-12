package com.dansplugins.fiefs.fief

import java.util.*

@JvmInline
value class MfFiefId(val value: String) {
    companion object {
        fun generate() = MfFiefId(UUID.randomUUID().toString())
    }
}