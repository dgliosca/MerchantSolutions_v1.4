package com.merchantsolutions.drivers.http

import com.merchantsolutions.AuctionJson.auto
import com.merchantsolutions.application.AuctionHub
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductToRegister
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

fun auctionApp(auctionHub: AuctionHub): RoutingHttpHandler {

    return routes(
        "/register-product" bind POST to { request ->
            val product = productToRegisterLens(request)
            auctionHub.add(product)
            Response(OK)
        },
        "/active-auctions" bind GET to { Response(OK).with(AuctionResult.lens of listOf()) },
        "/start-auction" bind POST to { Response(OK) },
        "/products" bind GET to {
            val listProducts = auctionHub.listProducts()
            Response(OK).with(listProductsLens of listProducts)
        }
    )
}

val listProductsLens = Body.auto<List<Product>>().toLens()
val productToRegisterLens = Body.auto<ProductToRegister>().toLens()

private class Auction
private class AuctionResult {
    companion object {
        val lens = Body.auto<List<Auction>>().toLens()
    }

}
