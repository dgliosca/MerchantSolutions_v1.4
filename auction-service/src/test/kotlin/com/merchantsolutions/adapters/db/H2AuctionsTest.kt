package com.merchantsolutions.adapters.db

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionState.closed
import com.merchantsolutions.domain.BidWithUser
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.Money.Companion.gbp
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductToRegister
import com.merchantsolutions.domain.UserId
import com.merchantsolutions.testing
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class H2AuctionsTest {
    private val storage: Storage = H2AuctionDatabase()
    private val auctions = H2Auctions(storage, testing)
    private val products = H2Products(storage, testing)

    @BeforeEach
    fun beforeEach() {
        storage.truncateTables()
    }

    @AfterAll
    fun afterAll() {
        storage.close()
    }

    @Test
    fun `can create an auction`() {
        val productId = products.add(
            ProductToRegister(
                "Candle Sticks",
                Money(gbp, BigDecimal("12.12"))
            )
        )

        val auctionId = auctions.createAuction(productId)

        assertThat(
            auctions.getAuction(auctionId),
            equalTo(Auction(auctionId, Product(productId, "Candle Sticks", Money(gbp, BigDecimal("12.12"))), closed))
        )
    }

    @Test
    fun `get winning bid`() {
        val productId = products.add(
            ProductToRegister(
                "Candle Sticks",
                Money(gbp, BigDecimal("12.12"))
            )
        )
        val auctionId = auctions.createAuction(productId)
        val userIdOne = UUID.fromString("00000000-0000-0000-0000-000000000000")
        val userIdTwo = UUID.fromString("00000000-0000-0000-0000-000000000001")
        auctions.addBid(BidWithUser(auctionId, UserId(userIdTwo), Money(gbp, BigDecimal("1.00"))))
        auctions.addBid(BidWithUser(auctionId, UserId(userIdOne), Money(gbp, BigDecimal("2.00"))))
        auctions.addBid(BidWithUser(auctionId, UserId(userIdTwo), Money(gbp, BigDecimal("3.00"))))
        auctions.addBid(BidWithUser(auctionId, UserId(userIdOne), Money(gbp, BigDecimal("2.50"))))

        val actual = auctions.winningBid(auctionId)
        println("actual = ${actual}")
        assertThat(
            actual,
            equalTo(BidWithUser(auctionId, UserId(userIdTwo), Money(gbp, BigDecimal("3.00"))))
        )
    }

    @Test
    fun `get all opened auctions`() {
        auctions.openAuction(
            auctions.createAuction(
                products.add(ProductToRegister("Candle Sticks", Money(gbp, BigDecimal("10.12"))))
            )
        )
        auctions.openAuction(
            auctions.createAuction(
                products.add(ProductToRegister("Antique Vase", Money(gbp, BigDecimal("11.12"))))
            )
        )
        auctions.openAuction(
            auctions.createAuction(
                products.add(ProductToRegister("Lost Ark", Money(gbp, BigDecimal("13.12"))))
            )
        )
        assertThat(auctions.openedAuctions(), hasSize(equalTo(3)))
    }

    @Test
    fun `can close an auction`() {
        val auctionId = auctions.createAuction(
            products.add(ProductToRegister("Candle Sticks", Money(gbp, BigDecimal("10.12"))))
        )
        auctions.openAuction(
            auctionId
        )
        assertThat(auctions.closeAuction(auctionId), equalTo(true))
    }
}