package com.merchantsolutions.adapters.db

import com.merchantsolutions.db.H2Transactor
import com.merchantsolutions.db.H2TxContext
import com.merchantsolutions.db.Transactor
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
    private val storage = H2AuctionDatabase()
    private val auctions = H2Auctions(testing)
    private val products = H2Products(testing)
    private val transactor = H2Transactor(storage.connection)

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
        transactor {
            val productId = products.add(
                it,
                ProductToRegister(
                    "Candle Sticks",
                    Money(gbp, BigDecimal("12.12"))
                )
            )

            val auctionId = auctions.createAuction(it, productId)

            assertThat(
                auctions.getAuction(it, auctionId),
                equalTo(
                    Auction(
                        auctionId,
                        Product(productId, "Candle Sticks", Money(gbp, BigDecimal("12.12"))),
                        closed
                    )
                )
            )
        }
    }

    @Test
    fun `get winning bid`() {
        transactor {
            val productId = products.add(
                it,
                ProductToRegister(
                    "Candle Sticks",
                    Money(gbp, BigDecimal("12.12"))
                )
            )
            val auctionId = auctions.createAuction(it, productId)
            val userIdOne = UUID.fromString("00000000-0000-0000-0000-000000000000")
            val userIdTwo = UUID.fromString("00000000-0000-0000-0000-000000000001")
            auctions.addBid(it, BidWithUser(auctionId, UserId(userIdTwo), Money(gbp, BigDecimal("1.00"))))
            auctions.addBid(it, BidWithUser(auctionId, UserId(userIdOne), Money(gbp, BigDecimal("2.00"))))
            auctions.addBid(it, BidWithUser(auctionId, UserId(userIdTwo), Money(gbp, BigDecimal("3.00"))))
            auctions.addBid(it, BidWithUser(auctionId, UserId(userIdOne), Money(gbp, BigDecimal("2.50"))))

            val actual = auctions.winningBid(it, auctionId)
            println("actual = ${actual}")
            assertThat(
                actual,
                equalTo(BidWithUser(auctionId, UserId(userIdTwo), Money(gbp, BigDecimal("3.00"))))
            )
        }
    }

    @Test
    fun `get all opened auctions`() {
        transactor {
            auctions.openAuction(
                it,
                auctions.createAuction(
                    it,
                    products.add(it, ProductToRegister("Candle Sticks", Money(gbp, BigDecimal("10.12"))))
                )
            )
            auctions.openAuction(
                it,
                auctions.createAuction(
                    it,
                    products.add(it, ProductToRegister("Antique Vase", Money(gbp, BigDecimal("11.12"))))
                )
            )
            auctions.openAuction(
                it,
                auctions.createAuction(
                    it,
                    products.add(it, ProductToRegister("Lost Ark", Money(gbp, BigDecimal("13.12"))))
                )
            )
            assertThat(auctions.openedAuctions(it), hasSize(equalTo(3)))
        }
    }

    @Test
    fun `can close an auction`() {
        transactor {
            val auctionId = auctions.createAuction(it,
                products.add(it, ProductToRegister("Candle Sticks", Money(gbp, BigDecimal("10.12"))))
            )
            auctions.openAuction(it, auctionId)
            assertThat(auctions.closeAuction(it, auctionId), equalTo(true))
        }
    }
}