package com.merchantsolutions.domain

import java.util.UUID

fun interface IdGenerator : ()-> UUID

val production get() = object : IdGenerator {
    override fun invoke(): UUID = UUID.randomUUID()
}