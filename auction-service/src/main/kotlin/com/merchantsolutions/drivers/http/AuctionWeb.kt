package com.merchantsolutions.drivers.http

import com.merchantsolutions.AuctionJson.auto
import com.merchantsolutions.AuctionJson.json
import com.merchantsolutions.adapters.db.*
import com.merchantsolutions.adapters.users.UsersClient
import com.merchantsolutions.application.AuctionHub
import com.merchantsolutions.domain.*
import com.merchantsolutions.domain.AuctionResult.AuctionClosed
import com.merchantsolutions.domain.AuctionResult.AuctionInProgress
import com.merchantsolutions.domain.AuctionResult.AuctionNotFound
import com.merchantsolutions.domain.AuctionState.closed
import org.http4k.client.OkHttp
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.FORBIDDEN
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.ServerFilters
import org.http4k.filter.ServerFilters.BearerAuth
import org.http4k.lens.bearerToken
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

fun AuctionApi(
    usersUri: Uri = Uri.of("http://user-service"),
    httpHandler: HttpHandler = OkHttp()
): RoutingHttpHandler {
    val h2AuctionDatabase = H2AuctionDatabase()
    return ServerFilters.CatchAll().then(
        auctionApp(
            AuctionHub(
                UsersClient(usersUri, httpHandler),
                H2Auctions(h2AuctionDatabase, production),
                H2Products(h2AuctionDatabase, production)
            )
        )
    )
}

fun auctionApp(auctionHub: AuctionHub): RoutingHttpHandler {
    val validateTokenFilter = BearerAuth { auctionHub.isValid(it) }
    return validateTokenFilter.then(
        routes(
            "/register-product" bind POST to { request ->
                val productToRegister = request.json<ProductToRegister>()
                val productId = auctionHub.add(productToRegister)
                Response(OK).with(productIdLens of productId)
            },
            "/create-auction" bind POST to { request ->
                val productId = request.json<ProductId>()
                val auctionId = auctionHub.createAuction(productId)
                Response(OK).with(auctionIdLens of auctionId)
            },
            "/active-auctions" bind GET to {
                Response(OK).with(
                    AuctionResultDto.lens of auctionHub.openedAuctions().toDto()
                )
            },
            "/start-auction" bind POST to { request ->
                val auctionId = request.json<AuctionId>()
                if (auctionHub.openAuctionFor(auctionId))
                    Response(OK)
                else
                    Response(CONFLICT)
            },
            "/products" bind GET to {
                val listProducts = auctionHub.listProducts()
                Response(OK).with(listProductsLens of listProducts)
            },
            "/close-auction" bind POST to { request ->
                val auctionId = request.json<AuctionId>()
                auctionHub.closeAuctionFor(auctionId)
                Response(OK)
            },
            "/auction-result" bind GET to { request ->
                val auctionId = request.json<AuctionId>()
                val auctionResultFor = auctionHub.auctionResultFor(auctionId)
                when (auctionResultFor) {
                    is AuctionClosed -> Response(OK).with(auctionClosedLens of auctionResultFor)
                    is AuctionInProgress -> Response(OK).with(auctionInProgressLens of auctionResultFor)
                    is AuctionNotFound, AuctionResult.AuctionClosedNoWinner -> Response(NOT_FOUND)
                }
            }, "/bid" bind POST to early@{ request ->
                val token = request.bearerToken() ?: return@early Response(FORBIDDEN)
                val userId = auctionHub.getUserByToken(token) ?: return@early Response(FORBIDDEN)
                val bid = request.json<Bid>()
                val result = auctionHub.add(BidWithUser(bid.auctionId, userId, bid.price))
                when (result) {
                    true -> Response(OK)
                    false -> Response(CONFLICT)
                }
            })
    )
}

private fun List<Auction>.toDto() = map {
    AuctionDto(
        it.auctionId,
        it.state
    )
}

data class AuctionDto(
    val auctionId: AuctionId,
    val state: AuctionState = closed
)

val auctionClosedLens = Body.auto<AuctionClosed>().toLens()
val auctionInProgressLens = Body.auto<AuctionInProgress>().toLens()
val auctionIdLens = Body.auto<AuctionId>().toLens()
val listProductsLens = Body.auto<List<Product>>().toLens()
val productIdLens = Body.auto<ProductId>().toLens()

private data class Bid(val auctionId: AuctionId, val price: Money)
private class AuctionResultDto {
    companion object {
        val lens = Body.auto<List<AuctionDto>>().toLens()
    }

}
