package com.merchantsolutions.adapters.db

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionId
import com.merchantsolutions.domain.AuctionState
import com.merchantsolutions.domain.BidWithUser
import com.merchantsolutions.domain.IdGenerator
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductId
import com.merchantsolutions.ports.Auctions
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
            val auctionId = AuctionId(UUID.fromString(rs.getString("id")))
            val productId = ProductId(UUID.fromString(rs.getString("id")))
            val description = rs.getString("description")
            val monetaryAmount = rs.getBigDecimal("minimum_selling_price")
            val currency = Currency.getInstance(rs.getString("currency"))
            Product(productId, description, Money(currency, monetaryAmount))
            val state = rs.getString("state")
            Auction(
                auctionId,
                Product(productId, description, Money(currency, monetaryAmount)),
                AuctionState.valueOf(state)
            )
        } else null
    }

    override fun createAuction(
        product: Product
    ): AuctionId {
        val selectSQL = "SELECT * FROM products WHERE id = '${product.productId.value}'";
        val rs = statement.executeQuery(selectSQL)
        if (rs.next() == false) {
            throw IllegalStateException("Product does not exist with id: ${product.productId.value}")
        }
        val auctionId = AuctionId(idGenerator())
        val result = statement.executeUpdate(
            "INSERT INTO auctions (id, product_id, state) VALUES ('${auctionId.value}', '${product.productId.value}', '${AuctionState.closed.name}');"
        )
        if (result != 1) {
            throw IllegalStateException("Couldn't create auction for product id: ${product.productId.value}")
        }
        return auctionId
    }

    override fun addBid(bid: BidWithUser) {
        TODO("Not yet implemented")
    }

    override fun remove(auction: Auction) {
        TODO("Not yet implemented")
    }

    override fun openedAuctions(): List<Auction> {
        TODO("Not yet implemented")
    }

    override fun winningBid(id: AuctionId): BidWithUser {
        TODO("Not yet implemented")
    }

    override fun openAuction(id: AuctionId): Boolean {
        TODO("Not yet implemented")
    }

    override fun closeAuction(id: AuctionId): Boolean {
        TODO("Not yet implemented")
    }
}