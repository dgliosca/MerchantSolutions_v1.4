package com.merchantsolutions.application

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductToRegister
import java.util.*


class AuctionHub {
    private val products = mutableListOf<Product>()
    private val auctions = mutableListOf<Auction>()
    fun add(product: ProductToRegister) {
        products.add(Product(UUID.randomUUID(), product.description))
    }

    fun listProducts(): List<Product> = products
    fun activateAuctionFor(id: UUID): Boolean = if (products.find { it.id == id } == null) {
        false
    } else {
        auctions.add(Auction(id))
        true
    }

    fun activeAuctions(): List<Auction> {
        return auctions
    }
}
