package com.merchantsolutions.domain

import com.merchantsolutions.domain.AuctionState.closed

data class Auction(
    val auctionId: AuctionId,
    val product: Product,
    val minimumSellingPrice: Money,
    val state: AuctionState = closed
)

enum class AuctionState {
    opened, closed
}