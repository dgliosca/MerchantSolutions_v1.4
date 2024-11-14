package com.merchantsolutions

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.http4k.format.ConfigurableJackson
import org.http4k.format.asConfigurable

object AuctionJson : ConfigurableJackson(
    KotlinModule.Builder().build().asConfigurable().done()
)