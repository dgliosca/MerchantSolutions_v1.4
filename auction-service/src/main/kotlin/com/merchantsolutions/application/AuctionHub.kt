package com.merchantsolutions.application

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.AuctionResult
import com.merchantsolutions.domain.AuctionState.closed
import com.merchantsolutions.domain.AuctionState.opened
import com.merchantsolutions.domain.BidWithUser
import com.merchantsolutions.domain.IdGenerator
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductToRegister
import com.merchantsolutions.domain.UserId
import java.util.*

class AuctionHub(val idGenerator: IdGenerator) {
    private val users = Users()
    private val bids = mutableListOf<BidWithUser>()
    private val products = mutableListOf<Product>()
    private val auctions = mutableListOf<Auction>()

    fun add(product: ProductToRegister) {
        products.add(Product(UUID.randomUUID(), product.description, product.minimumSellingPrice))
    }

    fun listProducts(): List<Product> = products
    fun activateAuctionFor(id: AuctionId): Boolean {
        val auction = auctions.find { it.auctionId == id }
        return if (auction == null) {
            false
        } else {
            auctions.remove(auction)
            auctions.add(auction.copy(state = opened))
            true
        }
    }

    fun activeAuctions(): List<Auction> = auctions.filter { it.state == opened }

    fun closeAuctionFor(productId: UUID) {
        val auction = auctions.find { it.productId == productId }
            ?: throw IllegalStateException("There is no auction for: $productId")
        auction.copy(state = closed)
        auctions.remove(auction)
        auctions.add(auction.copy(state = closed))
    }

    fun auctionResultFor(productId: UUID): AuctionResult {
        val auction = auctions.find { it.productId == productId }
            ?: throw IllegalStateException("There is no auction for: $productId")
        return when (auction.state) {
            opened -> {
                AuctionResult.AuctionInProgress
            }

            closed -> {
                val winningBid = bids.maxBy { it.price }
                AuctionResult.AuctionClosed(winningBid.userId, winningBid.price)
            }
        }
    }

    fun createAuction(productId: UUID): AuctionId {
        val product = products.find { it.id == productId }
        if (product == null) throw IllegalStateException("Auction cannot be crated because product doesn't exist with id: $productId")
        val auctionId = AuctionId(idGenerator())
        auctions.add(Auction(auctionId, productId, product.minimumSellingPrice))
        return auctionId
    }

    fun add(bid: BidWithUser): Boolean {
        val auction = auctions.find { it.auctionId == bid.auctionId }
        if (auction == null) throw IllegalStateException("Auction doesn't exist with id: ${bid.auctionId}")

        return if (bid.price < auction.minimumSellingPrice) {
            false
        } else {
            bids.add(bid)
            true
        }

    }

    data class User(val userId: UserId)

    class Users {
        val userOne = User(UserId(UUID.fromString("00000000-0000-0000-0000-000000000002")))
        val sellerUser = User(UserId(UUID.fromString("00000000-0000-0000-0000-000000000005")))
        val backOfficeUser = User(UserId(UUID.fromString("00000000-0000-0000-0000-000000000003")))
        private val users = listOf<User>(userOne)
        private val tokenToUsers = mutableMapOf<String, User>(
            "00000000-0000-0000-0000-000000000001" to userOne,
            "00000000-0000-0000-0000-000000000003" to backOfficeUser,
            "00000000-0000-0000-0000-000000000005" to sellerUser
        )

        fun isValid(token: String) = tokenToUsers[token] != null
        fun getUserByToken(token: String): User? {
            return tokenToUsers[token]
        }
    }

    fun getUserByToken(token: String) = if (users.isValid(token)) {
        users.getUserByToken(token)?.userId
    } else
        null

    fun isValid(token: String?): Boolean = token?.let { users.isValid(token) } == true
}
