package com.merchantsolutions.application

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductToRegister
import java.util.*
import com.merchantsolutions.domain.AuctionState.closed
import com.merchantsolutions.domain.AuctionState.opened
class AuctionHub {
    private val products = mutableListOf<Product>()
    private val auctions = mutableListOf<Auction>()

    fun add(product: ProductToRegister) {
        products.add(Product(UUID.randomUUID(), product.description))
    }

    fun listProducts(): List<Product> = products
    fun activateAuctionFor(id: UUID): Boolean {
        return if (products.find { it.id == id } == null) {
            false
        } else {
            val auction = Auction(id, opened)
            auctions.add(auction)
            true
        }
    }

    fun activeAuctions(): List<Auction> = auctions.filter { it.state == opened }

    fun closeAuctionFor(productId: UUID) {
        val auction = auctions.find { it.productId == productId }?: throw IllegalStateException("There is no auction for: $productId")
        auction.copy(state = closed)
        auctions.remove(auction)
        auctions.add(auction.copy(state = closed))
    }
}
