package com.merchantsolutions.domain

import java.util.UUID

data class ProductId(val value: UUID) {
    companion object {
        fun ProductId.Companion.of(uuid: UUID): ProductId = ProductId(uuid)
        fun ProductId.Companion.of(value: String): ProductId = ProductId(UUID.fromString(value))
    }
}