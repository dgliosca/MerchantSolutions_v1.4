package com.merchantsolutions

import com.merchantsolutions.AuctionJson.auto
import com.merchantsolutions.AuctionJson.json
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.ProductId
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import java.util.*

private val backOfficeProducts = Body.auto<List<BackOfficeProduct>>().toLens()
private val productIdLens = Body.auto<UUID>().toLens()
private val auctionIdLens = Body.auto<AuctionId>().toLens()

class BackOfficeActor(private val client: HttpHandler) {

    fun startAuction(id: AuctionId) {
        val result = client(Request(POST, "/start-auction").with(auctionIdLens of id))
        assertThat(result.status, equalTo(OK))
    }

    fun listProducts(): List<BackOfficeProduct> = backOfficeProducts(client(Request(GET, "/products")))
    fun closeAuction(id: UUID) {
        client(Request(POST, "/close-auction").with(productIdLens of id))
    }

    fun createAuction(product: ProductId) : AuctionId {
        val response = client(Request(POST, "/create-auction").with(productIdLens of product.value))
        return response.json<AuctionId>()
    }
}

data class BackOfficeProduct(val id: UUID, val description: String)