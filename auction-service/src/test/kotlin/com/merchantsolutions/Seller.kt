package com.merchantsolutions

import org.http4k.core.*
import com.merchantsolutions.AuctionJson.auto

class Seller(val client: HttpHandler) {

    fun registerProduct() {
        client(Request(Method.POST, "/register-product").with(Product.lens of Product("candle-sticks")))
    }

    data class Product(val description: String) {
        companion object {
            val lens = Body.auto<Product>().toLens()
        }
    }
}