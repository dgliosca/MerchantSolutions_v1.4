package com.merchantsolutions.adapters

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.AuctionState.closed
import com.merchantsolutions.domain.AuctionState.opened
import com.merchantsolutions.domain.BidWithUser
import com.merchantsolutions.domain.IdGenerator
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.ProductId
import com.merchantsolutions.ports.Auctions

class InMemoryAuctions(val idGenerator: IdGenerator) : Auctions {
    private val auctions = mutableListOf<Auction>()
    private val bids = mutableListOf<BidWithUser>()

    override fun getAuction(auctionId: AuctionId): Auction? {
        return auctions.find { it.auctionId == auctionId }
    }

    override fun createAuction(productId: ProductId, minimumSellingPrice: Money) : AuctionId{
        val auctionId = AuctionId(idGenerator())
        auctions.add(Auction(auctionId, productId, minimumSellingPrice))
        return auctionId
    }

    override fun remove(auction: Auction) {
        auctions.remove(auction)
    }

    override fun openedAuctions(): List<Auction> {
        return auctions.filter { it.state == opened }
    }

    override fun createAuction(bid: BidWithUser) {
        if (getAuction(bid.auctionId) != null)
            bids.add(bid)
    }

    override fun winningBid(id: AuctionId): BidWithUser {
        return bids.filter { it.auctionId == id }.maxBy { it.price }
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