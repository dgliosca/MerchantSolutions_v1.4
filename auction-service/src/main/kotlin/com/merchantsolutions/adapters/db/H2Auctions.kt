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
import java.sql.Statement
import java.util.Currency
import java.util.UUID

class H2Auctions(val statement: Statement, val idGenerator: IdGenerator) : Auctions {

    override fun getAuction(auctionId: AuctionId): Auction? {
        val selectSQL =
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
        val rs = statement.executeQuery(selectSQL)
        return if (rs.next()) {
            rs.toAuction()
        } else null
    }

    override fun createAuction(
        productId: ProductId
    ): AuctionId {
        val selectSQL = "SELECT * FROM products WHERE id = '${productId.value}'";
        val rs = statement.executeQuery(selectSQL)
        if (rs.next() == false) {
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
        val selectSQL =
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
        val rs = statement.executeQuery(selectSQL)
        while (rs.next()) {
            auctions.add(rs.toAuction())
        }
        return auctions
    }

    override fun winningBid(id: AuctionId): BidWithUser? {
        val rs = statement.executeQuery(
            """SELECT *
            FROM bids
            WHERE amount = (SELECT MAX(amount) FROM bids)
            ORDER BY id DESC
            LIMIT 1;"""
        )
        return if (rs.next()) {
            val auctionId = AuctionId(UUID.fromString(rs.getString("auction_id")))
            val userId = UserId(UUID.fromString(rs.getString("user_id")))
            val monetaryAmount = rs.getBigDecimal("amount")
            val currency = Currency.getInstance(rs.getString("currency"))
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