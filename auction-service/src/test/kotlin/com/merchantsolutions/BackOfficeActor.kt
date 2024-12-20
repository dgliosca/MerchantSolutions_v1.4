package com.merchantsolutions

import com.merchantsolutions.AuctionJson.auto
import com.merchantsolutions.AuctionJson.json
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.ProductId
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ClientFilters.BearerAuth
import org.http4k.filter.ClientFilters.BearerAuth.invoke

private val backOfficeProducts = Body.auto<List<BackOfficeProduct>>().toLens()
private val productIdLens = Body.auto<ProductId>().toLens()
private val auctionIdLens = Body.auto<AuctionId>().toLens()

class BackOfficeActor(http: HttpHandler) {
    val authenticatedHttp = BearerAuth("00000000-0000-0000-3333-000000000003")
        .then(http)

    fun startAuction(auctionId: AuctionId) {
        val result = authenticatedHttp(Request(POST, "/start-auction").with(auctionIdLens of auctionId))
        assertThat(result.status, equalTo(OK))
    }

    fun listProducts(): List<BackOfficeProduct> = backOfficeProducts(authenticatedHttp(Request(GET, "/products")))

    fun closeAuction(auctionId: AuctionId) {
        authenticatedHttp(Request(POST, "/close-auction").with(auctionIdLens of auctionId))
    }

    fun createAuction(productId: ProductId) : AuctionId {
        val response = authenticatedHttp(Request(POST, "/create-auction").with(productIdLens of productId))
        return response.json<AuctionId>()
    }
}

data class BackOfficeProduct(val productId: ProductId, val description: String, val minimumSellingPrice: Money)