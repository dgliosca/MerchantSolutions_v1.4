package com.merchantsolutions.application

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionOutcome
import com.merchantsolutions.domain.AuctionResult
import com.merchantsolutions.domain.AuctionState
import com.merchantsolutions.domain.AuctionState.closed
import com.merchantsolutions.domain.AuctionState.opened
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.Money.Companion.gbp
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductToRegister
import org.http4k.core.Response
import java.math.BigDecimal
import java.util.*

class AuctionHub() {
//    private val bids = mutableListOf<Bid>()
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

    fun auctionResultFor(productId: UUID): AuctionResult {
        val auction = auctions.find { it.productId == productId }?: throw IllegalStateException("There is no auction for: $productId")
        return when(auction.state) {
            opened -> AuctionResult.AuctionInProgress
            closed -> AuctionResult.AuctionClosed(AuctionOutcome.youLost, Money(gbp, BigDecimal(0.0)))
        }
    }
}
