package com.merchantsolutions.domain

import java.util.UUID

data class ProductId(val value: UUID) {
    companion object {
        fun of(uuid: UUID): ProductId = ProductId(uuid)
        fun of(value: String): ProductId = ProductId(UUID.fromString(value))
    }
}