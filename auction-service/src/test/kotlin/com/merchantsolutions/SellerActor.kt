package com.merchantsolutions

import org.http4k.core.*
import com.merchantsolutions.AuctionJson.auto
import org.http4k.core.Method.POST

class SellerActor(val client: HttpHandler) {

    fun registerProduct(product: Product) {
        client(Request(POST, "/register-product").with(Product.lens of product))
    }

    data class Product(val description: String) {
        companion object {
            val lens = Body.auto<Product>().toLens()
        }
    }
}