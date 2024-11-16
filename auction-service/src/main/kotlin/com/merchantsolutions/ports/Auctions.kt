package com.merchantsolutions.ports

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.BidWithUser
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.ProductId

interface Auctions {
    fun getAuction(auctionId: AuctionId): Auction?
    fun createAuction(productId: ProductId, minimumSellingPrice: Money) : AuctionId
    fun createAuction(bid: BidWithUser)
    fun remove(auction: Auction)
    fun activeAuctions(): List<Auction>
    fun winningBid(id: AuctionId) : BidWithUser
    fun openAuction(id: AuctionId): Boolean
    fun closeAuction(id: AuctionId) : Boolean
}