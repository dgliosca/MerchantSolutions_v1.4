package com.merchantsolutions

import com.merchantsolutions.domain.IdGenerator
import java.util.UUID

val testing get() = object : IdGenerator {
    private var count = 0L
    override fun invoke(): UUID = UUID(0, count++)
}