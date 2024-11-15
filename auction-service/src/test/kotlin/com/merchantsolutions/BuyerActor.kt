package com.merchantsolutions

import com.merchantsolutions.AuctionJson.auto
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.UserId
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.filter.ClientFilters
import java.util.*

class BuyerActor(httpHandler: HttpHandler) {
    private val http = ClientFilters.BearerAuth("buyer-1").then(httpHandler)

    fun listAuctions() = activeAuctions(http(Request(GET, "/active-auctions")))
    fun placeABid(auction: Auction, price: Money): Response {
        return http(Request(method = POST, "/bid").with(bidLens of Bid(auction.auctionId, price)))
    }

    fun auctionResult(auction: Auction): AuctionResult {
        val response = http(Request(GET, "/auction-result").with(id of auction.productId))
        return auctionResult(response)
    }

    private val activeAuctions = Body.auto<List<Auction>>().toLens()
    private val auctionResult = Body.auto<AuctionResult>().toLens()
    private val bidLens = Body.auto<Bid>().toLens()
    private val id = Body.auto<UUID>().toLens()
}

data class AuctionResult(val userId: UserId, val winningBid: Money)
enum class AuctionOutcome {
    youWin, youLost
}

data class Auction(val auctionId: AuctionId, val productId: UUID, val state: State)
data class Bid(val auctionId: AuctionId, val price: Money)
enum class State {
    opened, closed
}