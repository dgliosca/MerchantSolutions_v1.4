package com.merchantsolutions.domain

sealed class AuctionResult {
    data class AuctionClosed(val userId: UserId, val winningBid: Money) : AuctionResult()
    object AuctionInProgress : AuctionResult()
    object AuctionNotFound : AuctionResult()
}

