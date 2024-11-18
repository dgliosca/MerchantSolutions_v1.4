package com.merchantsolutions

import com.merchantsolutions.AuctionJson.auto
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.ProductId
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

class BuyerActor(val httpHandler: HttpHandler, val authToken: String = "00000000-0000-0000-0000-000000000001") {
    fun authenticated() = BuyerActor(
        BearerAuth(authToken)
            .then(httpHandler)
    )

    fun listAuctions() = openedAuctionsList(httpHandler(Request(GET, "/active-auctions")))

    fun placeABid(id: AuctionId, price: Money): Response =
        httpHandler(Request(method = POST, "/bid").with(bidLens of Bid(id, price)))

    fun auctionResult(auctionId: AuctionId): Response =
        httpHandler(Request(GET, "/auction-result").with(auctionIdLens of auctionId))

    private val openedAuctionsList = Body.auto<List<Auction>>().toLens()
    private val bidLens = Body.auto<Bid>().toLens()
    private val auctionIdLens = Body.auto<AuctionId>().toLens()
}

data class AuctionClosed(val userId: UserId, val winningBid: Money)
data class Auction(val auctionId: AuctionId, val state: State)
data class Bid(val auctionId: AuctionId, val price: Money)
enum class State {
    opened, closed
}