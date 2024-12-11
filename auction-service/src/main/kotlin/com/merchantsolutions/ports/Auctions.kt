package com.merchantsolutions.ports

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.BidWithUser
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductId

interface Auctions<TX> {
    fun getAuction(transactor: TX, auctionId: AuctionId): Auction?
    fun createAuction(transactor: TX, productId: ProductId): AuctionId
    fun addBid(transactor: TX, bid: BidWithUser)
    fun openedAuctions(transactor: TX): List<Auction>
    fun winningBid(transactor: TX, id: AuctionId): BidWithUser?
    fun openAuction(transactor: TX, id: AuctionId): Boolean
    fun closeAuction(transactor: TX, id: AuctionId): Boolean
}