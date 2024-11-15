package com.merchantsolutions.domain

data class BidWithUser(val auctionId: AuctionId, val userId: UserId, val price: Money)