package com.merchantsolutions.adapters.db

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
    private val storage: Storage = H2DB()
    private val products = H2Products(storage.statement, testing)

    @BeforeEach
    fun beforeEach() {
        storage.statement.execute("TRUNCATE TABLE PRODUCTS")
    }

    @AfterAll
    fun afterAll() {
        storage.close()
    }

    @Test
    fun `add products`() {
        val product = ProductToRegister("Candle Sticks", Money(gbp, BigDecimal("10.10")))
        val addedProduct = products.add(product)
        assertThat(
            products.get(addedProduct),
            equalTo(
                Product(
                    ProductId.of("00000000-0000-0000-0000-000000000000"),
                    "Candle Sticks",
                    product.minimumSellingPrice
                )
            )
        )
    }

    @Test
    fun `get all products`() {
        val productOne = ProductToRegister("Candle Sticks", Money(gbp, BigDecimal("10.10")))
        val productTwo = ProductToRegister("Antique Vase", Money(gbp, BigDecimal("5.10")))
        products.add(productOne)
        products.add(productTwo)

        assertThat(
            products.getProducts(),
            hasSize(equalTo(2))
        )
    }
}
