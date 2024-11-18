package com.merchantsolutions.application

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.AuctionResult
import com.merchantsolutions.domain.AuctionResult.AuctionClosedNoWinner
import com.merchantsolutions.domain.AuctionState.closed
import com.merchantsolutions.domain.AuctionState.opened
import com.merchantsolutions.domain.BidWithUser
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductId
import com.merchantsolutions.domain.ProductToRegister
import com.merchantsolutions.ports.Auctions
import com.merchantsolutions.ports.Products
import com.merchantsolutions.ports.Users

class AuctionHub(val users: Users, val auctions: Auctions, val products: Products) {

    fun add(product: ProductToRegister): ProductId {
        return products.add(product)
    }

    fun listProducts(): List<Product> = products.getProducts()

    fun openAuctionFor(id: AuctionId): Boolean {
        return auctions.openAuction(id)
    }

    fun openedAuctions(): List<Auction> = auctions.openedAuctions()

    fun closeAuctionFor(auctionId: AuctionId) {
        auctions.closeAuction(auctionId)
    }

    fun auctionResultFor(auctionId: AuctionId): AuctionResult {
        val auction = auctions.getAuction(auctionId) ?: return AuctionResult.AuctionNotFound
        return when (auction.state) {
            opened -> AuctionResult.AuctionInProgress
            closed -> {
                val winningBid = auctions.winningBid(auctionId)?: return AuctionClosedNoWinner
                AuctionResult.AuctionClosed(winningBid.userId, winningBid.price)
            }
        }
    }

    fun createAuction(productId: ProductId): AuctionId {
        val product = products.get(productId)
        if (product == null) throw IllegalStateException("Auction cannot be crated because product doesn't exist with id: $productId")

        val auctionId = auctions.createAuction(product.productId)
        return auctionId
    }

    fun add(bid: BidWithUser): Boolean {
        val auction = auctions.getAuction(bid.auctionId) ?: return false

        return if (bid.price < auction.product.minimumSellingPrice) {
            false
        } else {
            auctions.addBid(bid)
            true
        }
    }

    fun getUserByToken(token: String) = if (users.isValid(token)) {
        users.getUserByToken(token)?.userId
    } else
        null

    fun isValid(token: String?): Boolean = token?.let { users.isValid(token) } == true
}
