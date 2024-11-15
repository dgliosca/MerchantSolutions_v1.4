package com.merchantsolutions.domain

import com.merchantsolutions.domain.AuctionState.closed
import java.util.UUID

data class Auction(val productId: UUID, val state : AuctionState = closed)

enum class AuctionState {
    opened, closed
}