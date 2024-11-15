package com.merchantsolutions.application

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.AuctionOutcome
import com.merchantsolutions.domain.AuctionResult
import com.merchantsolutions.domain.AuctionState.closed
import com.merchantsolutions.domain.AuctionState.opened
import com.merchantsolutions.domain.IdGenerator
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.Money.Companion.gbp
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductToRegister
import java.math.BigDecimal
import java.util.*

class AuctionHub(val idGenerator: IdGenerator) {
    //    private val bids = mutableListOf<Bid>()
    private val products = mutableListOf<Product>()
    private val auctions = mutableListOf<Auction>()

    fun add(product: ProductToRegister) {
        products.add(Product(UUID.randomUUID(), product.description))
    }

    fun listProducts(): List<Product> = products
    fun activateAuctionFor(id: AuctionId): Boolean {
        val auction = auctions.find { it.auctionId == id }
        return if (auction == null) {
            false
        } else {
            auctions.remove(auction)
            auctions.add(auction.copy(state = opened))
            true
        }
    }

    fun activeAuctions(): List<Auction> = auctions.filter { it.state == opened }

    fun closeAuctionFor(productId: UUID) {
        val auction = auctions.find { it.productId == productId }
            ?: throw IllegalStateException("There is no auction for: $productId")
        auction.copy(state = closed)
        auctions.remove(auction)
        auctions.add(auction.copy(state = closed))
    }

    fun auctionResultFor(productId: UUID): AuctionResult {
        val auction = auctions.find { it.productId == productId }
            ?: throw IllegalStateException("There is no auction for: $productId")
        return when (auction.state) {
            opened -> AuctionResult.AuctionInProgress
            closed -> AuctionResult.AuctionClosed(AuctionOutcome.youLost, Money(gbp, BigDecimal(0.0)))
        }
    }

    fun createAuction(productId: UUID): AuctionId {
        val auctionId = AuctionId(idGenerator())
        auctions.add(Auction(auctionId, productId))
        return auctionId
    }
}
