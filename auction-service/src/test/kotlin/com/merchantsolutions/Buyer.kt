package com.merchantsolutions

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import com.merchantsolutions.AuctionJson.auto

class Buyer(val client: HttpHandler) {
    fun listAuctions() = activeAuctions(client(Request(Method.GET, "/active-auctions")))

    private val activeAuctions = Body.auto<List<Auction>>().toLens()
}

class Auction