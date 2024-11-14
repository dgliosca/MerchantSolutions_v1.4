package com.merchantsolutions

import com.merchantsolutions.AuctionJson.auto
import org.http4k.core.*
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

fun auctionApp(): RoutingHttpHandler {
    return routes(
        "/register-product" bind Method.POST to { Response(Status.OK) },
        "/active-auctions" bind Method.GET to { Response(Status.OK).with(AuctionResult.lens of listOf()) }
    )
}

private class Auction
private class AuctionResult {
    companion object {
        val lens = Body.auto<List<Auction>>().toLens()
    }

}
