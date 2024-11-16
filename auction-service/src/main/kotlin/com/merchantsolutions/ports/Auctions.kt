package com.merchantsolutions.ports

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionId

interface Auctions {
    fun getAuction(auctionId: AuctionId): Auction?
    fun add(auction: Auction)
    fun remove(auction: Auction)
    fun activeAuctions(): List<Auction>
}