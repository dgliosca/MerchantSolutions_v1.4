package com.merchantsolutions

import com.merchantsolutions.AuctionJson.auto
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.Money
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import java.util.*

class BuyerActor(val client: HttpHandler) {
    fun listAuctions() = activeAuctions(client(Request(GET, "/active-auctions")))
    fun placeABid(auction: Auction, price: Money): Response {
        return client(Request(method = POST, "/bid").with(bidLens of Bid(auction.productId, price)))
    }

    fun auctionResult(auction: Auction): AuctionResult {
        val response = client(Request(GET, "/auction-result").with(id of auction.productId))
        return auctionResult(response)
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

data class Auction(val auctionId: AuctionId, val productId: UUID, val state: State)
data class Bid(val productId: UUID, val price: Money)
enum class State {
    opened, closed
}