package com.merchantsolutions

import com.merchantsolutions.AuctionJson.auto
import com.merchantsolutions.domain.Money
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.*
import org.http4k.core.Method.GET
import java.util.*

class BuyerActor(val client: HttpHandler) {
    fun listAuctions() = activeAuctions(client(Request(GET, "/active-auctions")))
    fun placeABid(auction: Auction, price: Money): Response {
        return client(Request(Method.POST, "/bid").with(bidLens of Bid(auction.productId, price)))
    }

    fun hasLostAuction(auction: Auction) {
        val response = client(Request(GET, "/auction-result").with(id of auction.productId))
        assertThat(auctionResult(response).outcome, equalTo(AuctionOutcome.youLost))
    }

    private val activeAuctions = Body.auto<List<Auction>>().toLens()
    private val auctionResult = Body.auto<AuctionResult>().toLens()
    private val bidLens = Body.auto<Bid>().toLens()
    private val id = Body.auto<UUID>().toLens()
}

data class AuctionResult(val outcome: AuctionOutcome, val winningBid: Money)
enum class AuctionOutcome {
    youWin, youLost
}

data class Auction(val productId: UUID, val state: State)
data class Bid(val productId: UUID, val price: Money)
enum class State {
    opened, closed
}