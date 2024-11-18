package com.merchantsolutions.ports

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.BidWithUser
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductId

interface Auctions {
    fun getAuction(auctionId: AuctionId): Auction?
    fun createAuction(productId: ProductId) : AuctionId
    fun addBid(bid: BidWithUser)
    fun openedAuctions(): List<Auction>
    fun winningBid(id: AuctionId) : BidWithUser
    fun openAuction(id: AuctionId): Boolean
    fun closeAuction(id: AuctionId) : Boolean
}