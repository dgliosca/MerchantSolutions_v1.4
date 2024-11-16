package com.merchantsolutions

import com.merchantsolutions.AuctionJson.auto
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.UserId
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ClientFilters.BearerAuth
import java.util.UUID

class BuyerActor(val httpHandler: HttpHandler, val authToken: String = "00000000-0000-0000-0000-000000000001") {
    fun authenticated() = BuyerActor(BearerAuth(authToken)
        .then(httpHandler))
    fun notAuthenticated() = BuyerActor(httpHandler)

    fun listAuctions() = activeAuctions(httpHandler(Request(GET, "/active-auctions")))
    fun placeABid(id: AuctionId, price: Money): Response {
        return httpHandler(Request(method = POST, "/bid").with(bidLens of Bid(id, price)))
    }

    fun auctionResult(auctionId: AuctionId): AuctionResult {
        val response = httpHandler(Request(GET, "/auction-result").with(auctionIdLens of auctionId))
        return auctionResult(response)
    }

    private val activeAuctions = Body.auto<List<Auction>>().toLens()
    private val auctionResult = Body.auto<AuctionResult>().toLens()
    private val bidLens = Body.auto<Bid>().toLens()
    private val auctionIdLens = Body.auto<AuctionId>().toLens()
}

data class AuctionResult(val userId: UserId, val winningBid: Money)

data class Auction(val auctionId: AuctionId, val productId: UUID, val minimumSellingPrice: Money, val state: State)
data class Bid(val auctionId: AuctionId, val price: Money)
enum class State {
    opened, closed
}