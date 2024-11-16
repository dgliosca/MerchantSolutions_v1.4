package com.merchantsolutions

import org.http4k.core.*
import com.merchantsolutions.AuctionJson.auto
import org.http4k.core.Method.POST
import org.http4k.filter.ClientFilters.BearerAuth
import org.http4k.filter.ClientFilters.BearerAuth.invoke
import org.http4k.metrics.MetricsDefaults.Companion.client

class SellerActor(val httpHandler: HttpHandler) {
    val http = BearerAuth("00000000-0000-0000-0000-000000000005")
        .then(httpHandler)

    fun registerProduct(product: Product) {
        http(Request(POST, "/register-product").with(Product.lens of product))
    }

    data class Product(val description: String) {
        companion object {
            val lens = Body.auto<Product>().toLens()
        }
    }
}