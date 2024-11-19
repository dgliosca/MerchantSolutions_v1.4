package com.merchantsolutions.adapters.db

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.AuctionState
import com.merchantsolutions.domain.BidWithUser
import com.merchantsolutions.domain.IdGenerator
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductId
import com.merchantsolutions.domain.UserId
import com.merchantsolutions.ports.Auctions
import java.sql.ResultSet
import java.util.Currency
import java.util.UUID

class H2Auctions(private val storage: Storage, private val idGenerator: IdGenerator) : Auctions {
    private val statement = storage.statement

    override fun getAuction(auctionId: AuctionId): Auction? {
        val result = statement.executeQuery(
            """SELECT
                a.id AS auction_id,
                a.state,
                p.id AS product_id,
                p.description,
                p.minimum_selling_price,
                p.currency
            FROM
                auctions a
            JOIN products p ON a.product_id = p.id
            WHERE
                a.id = '${auctionId.value}';"""
        )
        return if (result.next()) {
            result.toAuction()
        } else null
    }

    override fun createAuction(
        productId: ProductId
    ): AuctionId {
        if (!statement.executeQuery("SELECT * FROM products WHERE id = '${productId.value}'").next()) {
            throw IllegalStateException("Product does not exist with id: ${productId.value}")
        }
        val auctionId = AuctionId(idGenerator())
        val result = statement.executeUpdate(
            "INSERT INTO auctions (id, product_id, state) VALUES ('${auctionId.value}', '${productId.value}', '${AuctionState.closed.name}');"
        )
        if (result != 1) {
            throw IllegalStateException("Couldn't create auction for product id: ${productId.value}")
        }
        return auctionId
    }

    override fun addBid(bid: BidWithUser) {
        statement.executeUpdate(
            "INSERT INTO bids (user_id, auction_id, amount, currency)\n" +
                    "VALUES ('${bid.userId.value}', '${bid.auctionId.value}', ${bid.price.amount}, '${bid.price.currency}');"
        )
    }

    override fun openedAuctions(): List<Auction> {
        val auctions = mutableListOf<Auction>()
        val result = statement.executeQuery(
            """SELECT
                a.id AS auction_id,
                a.state,
                p.id AS product_id,
                p.description,
                p.minimum_selling_price,
                p.currency
            FROM
                auctions a
            JOIN products p ON a.product_id = p.id
            WHERE a.state = 'opened';"""
        )
        while (result.next()) {
            auctions.add(result.toAuction())
        }
        return auctions
    }

    override fun winningBid(id: AuctionId): BidWithUser? {
        val result = statement.executeQuery(
            """SELECT *
            FROM bids
            WHERE amount = (SELECT MAX(amount) FROM bids)
            ORDER BY id ASC
            LIMIT 1;"""
        )
        return if (result.next()) {
            val auctionId = AuctionId(UUID.fromString(result.getString("auction_id")))
            val userId = UserId(UUID.fromString(result.getString("user_id")))
            val monetaryAmount = result.getBigDecimal("amount")
            val currency = Currency.getInstance(result.getString("currency"))
            BidWithUser(
                auctionId,
                userId,
                Money(currency, monetaryAmount)
            )
        } else null
    }

    override fun openAuction(id: AuctionId): Boolean {
        val result = statement.executeUpdate("""UPDATE auctions SET state = 'opened' WHERE id = '${id.value}';""")
        return result == 1
    }

    override fun closeAuction(id: AuctionId): Boolean {
        val result = statement.executeUpdate("""UPDATE auctions SET state = 'closed' WHERE id = '${id.value}';""")
        return result == 1
    }
}

private fun ResultSet.toAuction(): Auction {
    val auctionId = AuctionId(UUID.fromString(getString("id")))
    val productId = ProductId(UUID.fromString(getString("id")))
    val description = getString("description")
    val monetaryAmount = getBigDecimal("minimum_selling_price")
    val currency = Currency.getInstance(getString("currency"))
    Product(productId, description, Money(currency, monetaryAmount))
    val state = getString("state")
    return Auction(
        auctionId,
        Product(productId, description, Money(currency, monetaryAmount)),
        AuctionState.valueOf(state)
    )
}