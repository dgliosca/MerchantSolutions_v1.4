package com.merchantsolutions.ports

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.BidWithUser

interface Auctions {
    fun getAuction(auctionId: AuctionId): Auction?
    fun add(auction: Auction)
    fun add(bid: BidWithUser)
    fun remove(auction: Auction)
    fun activeAuctions(): List<Auction>
    fun winningBid(id: AuctionId) : BidWithUser
    fun activateAuction(id: AuctionId): Boolean
    fun closeAuction(id: AuctionId) : Boolean
}