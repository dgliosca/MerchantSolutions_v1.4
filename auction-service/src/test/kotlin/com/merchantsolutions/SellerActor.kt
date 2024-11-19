package com.merchantsolutions

import com.merchantsolutions.AuctionJson.auto
import com.merchantsolutions.AuctionJson.json
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.ProductId
import org.http4k.core.*
import org.http4k.core.Method.POST
import org.http4k.filter.ClientFilters.BearerAuth
import org.http4k.filter.ClientFilters.BearerAuth.invoke

class SellerActor(httpHandler: HttpHandler) {
    val http = BearerAuth("00000000-0000-0000-5555-000000000005")
        .then(httpHandler)

    fun registerProduct(product: Product) : ProductId {
        val response = http(Request(POST, "/register-product").with(Product.lens of product))
        return response.json<ProductId>()
    }

    data class Product(val description: String, val minimumSellingPrice: Money) {
        companion object {
            val lens = Body.auto<Product>().toLens()
        }
    }
}