package com.merchantsolutions.domain

import com.merchantsolutions.domain.AuctionState.closed
import java.util.UUID

data class Auction(
    val auctionId: AuctionId,
    val productId: UUID,
    val minimumSellingPrice: Money,
    val state: AuctionState = closed
)

enum class AuctionState {
    opened, closed
}