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
import java.util.*

private val backOfficeProducts = Body.auto<List<BackOfficeProduct>>().toLens()
private val productIdLens = Body.auto<UUID>().toLens()
private val productIdLens2 = Body.auto<ProductId>().toLens()
private val auctionIdLens = Body.auto<AuctionId>().toLens()

class BackOfficeActor(http: HttpHandler) {
    val authenticatedHttp = BearerAuth("00000000-0000-0000-0000-000000000003")
        .then(http)

    fun startAuction(id: AuctionId) {
        val result = authenticatedHttp(Request(POST, "/start-auction").with(auctionIdLens of id))
        assertThat(result.status, equalTo(OK))
    }

    fun listProducts(): List<BackOfficeProduct> = backOfficeProducts(authenticatedHttp(Request(GET, "/products")))
    fun closeAuction(productId: ProductId) {
        authenticatedHttp(Request(POST, "/close-auction").with(productIdLens2 of productId))
    }

    fun createAuction(productId: ProductId) : AuctionId {
        val response = authenticatedHttp(Request(POST, "/create-auction").with(productIdLens2 of productId))
        return response.json<AuctionId>()
    }
}

data class BackOfficeProduct(val id: UUID, val description: String, val minimumSellingPrice: Money)