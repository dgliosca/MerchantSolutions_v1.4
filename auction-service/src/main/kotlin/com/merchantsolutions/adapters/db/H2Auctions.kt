package com.merchantsolutions.adapters.db

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.BidWithUser
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.ProductId
import com.merchantsolutions.ports.Auctions

class H2Auctions : Auctions {
    override fun getAuction(auctionId: AuctionId): Auction? {
        TODO("Not yet implemented")
    }

    override fun createAuction(
        productId: ProductId,
        minimumSellingPrice: Money
    ): AuctionId {
        TODO("Not yet implemented")
    }

    override fun createAuction(bid: BidWithUser) {
        TODO("Not yet implemented")
    }

    override fun remove(auction: Auction) {
        TODO("Not yet implemented")
    }

    override fun activeAuctions(): List<Auction> {
        TODO("Not yet implemented")
    }

    override fun winningBid(id: AuctionId): BidWithUser {
        TODO("Not yet implemented")
    }

    override fun openAuction(id: AuctionId): Boolean {
        TODO("Not yet implemented")
    }

    override fun closeAuction(id: AuctionId): Boolean {
        TODO("Not yet implemented")
    }
}