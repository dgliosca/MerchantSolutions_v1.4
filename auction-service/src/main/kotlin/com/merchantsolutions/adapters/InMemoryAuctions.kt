package com.merchantsolutions.adapters

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.AuctionState.opened
import com.merchantsolutions.domain.BidWithUser
import com.merchantsolutions.ports.Auctions

class InMemoryAuctions : Auctions {
    private val auctions = mutableListOf<Auction>()
    private val bids = mutableListOf<BidWithUser>()

    override fun getAuction(auctionId: AuctionId): Auction? {
        return auctions.find { it.auctionId == auctionId }
    }

    override fun add(auction: Auction) {
        auctions.add(auction)
    }

    override fun remove(auction: Auction) {
        auctions.remove(auction)
    }

    override fun activeAuctions(): List<Auction> {
        return auctions.filter { it.state == opened }
    }

    override fun add(bid: BidWithUser) {
        if (getAuction(bid.auctionId) != null)
            bids.add(bid)
    }

    override fun winningBid(id: AuctionId): BidWithUser {
        return bids.filter { it.auctionId == id }.maxBy { it.price }
    }

    override fun activateAuction(id: AuctionId): Boolean {
        val auction = getAuction(id)
        return if (auction == null) {
            false
        } else {
            auctions.remove(auction)
            auctions.add(auction.copy(state = opened))
            true
        }
    }
}