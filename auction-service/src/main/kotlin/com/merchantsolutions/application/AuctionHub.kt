package com.merchantsolutions.application

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.AuctionResult
import com.merchantsolutions.domain.AuctionState.closed
import com.merchantsolutions.domain.AuctionState.opened
import com.merchantsolutions.domain.BidWithUser
import com.merchantsolutions.domain.IdGenerator
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductId
import com.merchantsolutions.domain.ProductToRegister
import com.merchantsolutions.ports.Auctions
import com.merchantsolutions.ports.Products
import com.merchantsolutions.ports.Users

class AuctionHub(val idGenerator: IdGenerator, val users: Users, val auctions: Auctions, val products: Products) {

    fun add(product: ProductToRegister): ProductId {
        return products.add(product)
    }

    fun listProducts(): List<Product> = products.getProducts()

    fun openAuctionFor(id: AuctionId): Boolean {
        return auctions.activateAuction(id)
    }

    fun activeAuctions(): List<Auction> = auctions.activeAuctions()

    fun closeAuctionFor(auctionId: AuctionId) {
        val auction = auctions.getAuction(auctionId)
            ?: throw IllegalStateException("There is no auction for: $auctionId")
        auctions.remove(auction)
        auctions.add(auction.copy(state = closed))
    }

    fun auctionResultFor(auctionId: AuctionId): AuctionResult {
        val auction = auctions.getAuction(auctionId)
            ?: throw IllegalStateException("There is no auction for: $auctionId")
        return when (auction.state) {
            opened -> {
                AuctionResult.AuctionInProgress
            }

            closed -> {
                val winningBid = auctions.winningBid(auctionId)
                AuctionResult.AuctionClosed(winningBid.userId, winningBid.price)
            }
        }
    }

    fun createAuction(productId: ProductId): AuctionId {
        val product = products.get(productId)
        if (product == null) throw IllegalStateException("Auction cannot be crated because product doesn't exist with id: $productId")
        val auctionId = AuctionId(idGenerator())
        auctions.add(Auction(auctionId, productId.value, product.minimumSellingPrice))
        return auctionId
    }

    fun add(bid: BidWithUser): Boolean {
        val auction = auctions.getAuction(bid.auctionId)
        if (auction == null) throw IllegalStateException("Auction doesn't exist with id: ${bid.auctionId}")

        return if (bid.price < auction.minimumSellingPrice) {
            false
        } else {
            auctions.add(bid)
            true
        }

    }

    fun getUserByToken(token: String) = if (users.isValid(token)) {
        users.getUserByToken(token)?.userId
    } else
        null

    fun isValid(token: String?): Boolean = token?.let { users.isValid(token) } == true
}
