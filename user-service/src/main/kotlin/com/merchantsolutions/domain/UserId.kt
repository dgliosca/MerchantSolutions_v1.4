package com.merchantsolutions.domain

import java.util.UUID

data class UserId(val value: UUID) {
    companion object {
        fun of(uuid: UUID): UserId = UserId(uuid)
        fun of(value: String): UserId = UserId(UUID.fromString(value))
    }
}