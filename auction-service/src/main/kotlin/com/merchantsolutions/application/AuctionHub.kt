package com.merchantsolutions.application

import com.merchantsolutions.db.Transactor
import com.merchantsolutions.domain.*
import com.merchantsolutions.domain.AuctionResult.AuctionClosedNoWinner
import com.merchantsolutions.domain.AuctionState.closed
import com.merchantsolutions.domain.AuctionState.opened
import com.merchantsolutions.ports.Auctions
import com.merchantsolutions.ports.Products
import com.merchantsolutions.ports.Users

class AuctionHub<TX>(
    val users: Users,
    val auctions: Auctions<TX>,
    val products: Products<TX>,
    val transactor: Transactor<TX>
) {

    fun add(product: ProductToRegister): ProductId {
        return transactor {
            products.add(it, product)
        }
    }

    fun listProducts(): List<Product> = transactor { products.getProducts(it) }

    fun openAuctionFor(id: AuctionId): Boolean {
        return transactor {
            auctions.openAuction(it, id)
        }
    }

    fun openedAuctions(): List<Auction> = transactor { auctions.openedAuctions(it) }

    fun closeAuctionFor(auctionId: AuctionId) {
        transactor {
            auctions.closeAuction(it, auctionId)
        }
    }

    fun auctionResultFor(auctionId: AuctionId): AuctionResult {
        return transactor {
            val auction = auctions.getAuction(it, auctionId) ?: return@transactor AuctionResult.AuctionNotFound
            when (auction.state) {
                opened -> AuctionResult.AuctionInProgress
                closed -> {
                    val winningBid = auctions.winningBid(it, auctionId) ?: return@transactor AuctionClosedNoWinner
                    AuctionResult.AuctionClosed(winningBid.userId, winningBid.price)
                }
            }
        }
    }

    fun createAuction(productId: ProductId): AuctionId {
        return transactor {
            val product = products.get(it, productId)
                ?: throw IllegalStateException("Auction cannot be crated because product doesn't exist with id: $productId")

            val auctionId = auctions.createAuction(it, product.productId)
            auctionId
        }
    }

    fun add(bid: BidWithUser): Boolean {
        return transactor {
            val auction = auctions.getAuction(it, bid.auctionId) ?: return@transactor false
            if (auction.state == closed)
                return@transactor false
            if (bid.price < auction.product.minimumSellingPrice) {
                false
            } else {
                auctions.addBid(it, bid)
                true
            }
        }
    }

    fun getUserByToken(token: String): UserId? {
        return if (users.isValid(token)) {
            users.getUserByToken(token)?.userId
        } else
            null
    }

    fun isValid(token: String?): Boolean = token?.let { users.isValid(token) } == true
}
