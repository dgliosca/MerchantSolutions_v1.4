package com.merchantsolutions

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import com.merchantsolutions.AuctionJson.auto
import java.util.*

class BuyerActor(val client: HttpHandler) {
    fun listAuctions() = activeAuctions(client(Request(Method.GET, "/active-auctions")))

    private val activeAuctions = Body.auto<List<Auction>>().toLens()
}

data class Auction(val productId: UUID)