package com.merchantsolutions.domain

sealed class AuctionResult {
    data class AuctionClosed(val outcome: AuctionOutcome, val winningBid: Money) : AuctionResult()
    object AuctionInProgress : AuctionResult()
}

enum class AuctionOutcome {
    youWin, youLost
}