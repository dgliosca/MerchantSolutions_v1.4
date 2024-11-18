package com.merchantsolutions.adapters.db

import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.Money.Companion.gbp
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductId
import com.merchantsolutions.domain.ProductToRegister
import com.merchantsolutions.testing
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class H2ProductsTest {
    val storage = H2DB()
    val products = H2Products(storage.statement, testing)

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
}
