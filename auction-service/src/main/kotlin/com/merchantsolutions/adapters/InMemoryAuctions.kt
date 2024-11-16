package com.merchantsolutions.adapters

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.AuctionState.opened
import com.merchantsolutions.ports.Auctions

class InMemoryAuctions : Auctions {
    private val auctions = mutableListOf<Auction>()

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
}