package com.merchantsolutions.domain

import java.util.UUID

data class ProductId(val value: UUID) {
    companion object {
        fun ProductId.Companion.of(uuid: UUID): ProductId = ProductId(uuid)
    }
}