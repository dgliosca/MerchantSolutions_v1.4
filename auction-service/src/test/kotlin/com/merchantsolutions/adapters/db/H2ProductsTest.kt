package com.merchantsolutions.adapters.db

import com.merchantsolutions.db.H2Transactor
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.Money.Companion.gbp
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductId
import com.merchantsolutions.domain.ProductToRegister
import com.merchantsolutions.testing
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class H2ProductsTest {
    private val storage = H2AuctionDatabase()
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
    fun `add products`() {
        transactor {
            val product = ProductToRegister("Candle Sticks", Money(gbp, BigDecimal("10.10")))
            val addedProduct = products.add(it, product)
            assertThat(
                products.get(it, addedProduct),
                equalTo(
                    Product(
                        ProductId.of("00000000-0000-0000-0000-000000000000"),
                        "Candle Sticks",
                        product.minimumSellingPrice
                    )
                )
            )
        }
    }

    @Test
    fun `get all products`() {
        transactor {
            val productOne = ProductToRegister("Candle Sticks", Money(gbp, BigDecimal("10.10")))
            val productTwo = ProductToRegister("Antique Vase", Money(gbp, BigDecimal("5.10")))
            products.add(it, productOne)
            products.add(it, productTwo)

            assertThat(
                products.getProducts(it),
                hasSize(equalTo(2))
            )
        }
    }
}
