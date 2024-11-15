package com.merchantsolutions

import com.merchantsolutions.AuctionJson.auto
import com.merchantsolutions.domain.Money
import org.http4k.core.*
import org.http4k.core.Method.GET
import java.util.*

class BuyerActor(val client: HttpHandler) {
    fun listAuctions() = activeAuctions(client(Request(GET, "/active-auctions")))
    fun placeABid(auction: Auction, price: Money): Response {
        return client(Request(Method.POST, "/bid").with(bidLens of Bid(auction.productId, price)))
    }

    private val activeAuctions = Body.auto<List<Auction>>().toLens()
    private val bidLens = Body.auto<Bid>().toLens()
}

data class Auction(val productId: UUID, val state: State)
data class Bid(val productId: UUID, val price: Money)
enum class State {
    opened, closed
}