package com.merchantsolutions.adapters.db

import com.merchantsolutions.domain.Auction
import com.merchantsolutions.domain.AuctionState.closed
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.Money.Companion.gbp
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductToRegister
import com.merchantsolutions.testing
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class H2AuctionsTest {
    private val storage: Storage = H2DB()
    private val auctions = H2Auctions(storage.statement, testing)
    private val products = H2Products(storage.statement, testing)

    @BeforeEach
    fun beforeEach() {
        storage.statement.execute("TRUNCATE TABLE AUCTIONS")
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
}