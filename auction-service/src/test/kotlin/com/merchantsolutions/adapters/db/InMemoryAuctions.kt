package com.merchantsolutions.adapters.db

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.AuctionState.closed
import com.merchantsolutions.domain.AuctionState.opened
import com.merchantsolutions.domain.BidWithUser
import com.merchantsolutions.domain.IdGenerator
import com.merchantsolutions.domain.ProductId
import com.merchantsolutions.ports.Auctions
import com.merchantsolutions.ports.Products

class InMemoryAuctions(val idGenerator: IdGenerator, val products: Products) : Auctions {
    private val auctions = mutableListOf<Auction>()
    private val bids = mutableListOf<BidWithUser>()

    override fun getAuction(auctionId: AuctionId): Auction? {
        return auctions.find { it.auctionId == auctionId }
    }

    override fun createAuction(productId: ProductId): AuctionId {
        val auctionId = AuctionId(idGenerator())
        val product = products.get(productId)?: throw IllegalStateException("Cannot create auction for product: ${productId.value}")
        auctions.add(Auction(auctionId, product))
        return auctionId
    }

    override fun openedAuctions(): List<Auction> {
        return auctions.filter { it.state == opened }
    }

    override fun addBid(bid: BidWithUser) {
        if (getAuction(bid.auctionId) != null)
            bids.add(bid)
    }

    override fun winningBid(id: AuctionId): BidWithUser? {
        return try {
            bids.filter { it.auctionId == id }.maxBy { it.price }
        } catch (e: Exception) {
            null
        }
    }

    override fun openAuction(id: AuctionId): Boolean {
        val auction = getAuction(id)
        return if (auction == null) {
            false
        } else {
            auctions.remove(auction)
            auctions.add(auction.copy(state = opened))
            true
        }
    }

    override fun closeAuction(id: AuctionId): Boolean {
        val auction = getAuction(id) ?: return false
        auctions.remove(auction)
        auctions.add(auction.copy(state = closed))
        return true
    }
}