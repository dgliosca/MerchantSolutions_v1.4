package com.merchantsolutions.drivers.http

import com.merchantsolutions.AuctionJson.auto
import com.merchantsolutions.application.AuctionHub
import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionResult.AuctionClosed
import com.merchantsolutions.domain.AuctionResult.AuctionInProgress
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductToRegister
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.util.*
import com.merchantsolutions.AuctionJson.json
import com.merchantsolutions.domain.AuctionId

fun auctionApp(auctionHub: AuctionHub): RoutingHttpHandler {

    return routes(
        "/register-product" bind POST to { request ->
            val productToRegister = request.json<ProductToRegister>()
            auctionHub.add(productToRegister)
            Response(OK)
        },
        "/create-auction" bind POST to { request->
            val productId = request.json<UUID>()
            val auctionId = auctionHub.createAuction(productId)
            Response(OK).with(auctionIdLens of auctionId)
        },
        "/active-auctions" bind GET to { Response(OK).with(AuctionResult.lens of auctionHub.activeAuctions()) },
        "/start-auction" bind POST to { request ->
            val auctionId = request.json<AuctionId>()
            if (auctionHub.activateAuctionFor(auctionId))
                Response(OK)
            else
                Response(CONFLICT)
        },
        "/products" bind GET to {
            val listProducts = auctionHub.listProducts()
            Response(OK).with(listProductsLens of listProducts)
        },
        "/close-auction" bind POST to { request ->
            val id = uuid(request)
            auctionHub.closeAuctionFor(id)
            Response(OK)
        },
        "/auction-result" bind GET to { request ->
            val productId = uuid(request)
            val auctionResultFor = auctionHub.auctionResultFor(productId)
            when (auctionResultFor) {
                is AuctionClosed -> Response(OK).with(auctionClosedLens of auctionResultFor)
                is AuctionInProgress -> Response(OK).with(auctionInProgressLens of auctionResultFor)
            }
        }
    )
}

val auctionClosedLens = Body.auto<AuctionClosed>().toLens()
val auctionInProgressLens = Body.auto<AuctionInProgress>().toLens()
val auctionIdLens = Body.auto<AuctionId>().toLens()
val listProductsLens = Body.auto<List<Product>>().toLens()
val uuid = Body.auto<UUID>().toLens()

private class AuctionResult {
    companion object {
        val lens = Body.auto<List<Auction>>().toLens()
    }

}
