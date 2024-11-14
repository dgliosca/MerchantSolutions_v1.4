package com.merchantsolutions

import com.merchantsolutions.AuctionJson.auto
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.with
import java.util.*

private val backOfficeProducts = Body.auto<List<BackOfficeProduct>>().toLens()

class BackOfficeActor(private val client: HttpHandler) {

    fun startAuction(id: UUID) {
        client(Request(POST, "/start-auction").with(Body.auto<UUID>().toLens() of id))
    }

    fun listProducts(): List<BackOfficeProduct> = backOfficeProducts(client(Request(GET, "/products")))
}

data class BackOfficeProduct(val id: UUID, val description: String)