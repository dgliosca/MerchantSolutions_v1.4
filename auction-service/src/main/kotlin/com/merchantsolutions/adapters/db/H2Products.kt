package com.merchantsolutions.adapters.db

import com.merchantsolutions.domain.IdGenerator
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductId
import com.merchantsolutions.domain.ProductToRegister
import com.merchantsolutions.ports.Products
import java.util.Currency
import java.util.UUID

class H2Products(storage: Storage, private val idGenerator: IdGenerator) : Products {

    private val statement = storage.statement

    override fun getProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val selectSQL = "SELECT * FROM products"
        val result = statement.executeQuery(selectSQL)

        while (result.next()) {
            val productId = ProductId(UUID.fromString(result.getString("id")))
            val description = result.getString("description")
            val monetaryAmount = result.getBigDecimal("minimum_selling_price")
            val currency = Currency.getInstance(result.getString("currency"))
            val product = Product(productId, description, Money(currency, monetaryAmount))
            products.add(product)
        }

        return products
    }

    override fun get(productId: ProductId): Product? {
        val selectSQL = "SELECT * FROM products WHERE id = '${productId.value}'";
        val result = statement.executeQuery(selectSQL)
        return if (result.next()) {
            val description = result.getString("description")
            val monetaryAmount = result.getBigDecimal("minimum_selling_price")
            val currency = Currency.getInstance(result.getString("currency"))
            Product(productId, description, Money(currency, monetaryAmount))
        } else
            null
    }

    override fun add(product: ProductToRegister): ProductId {
        val productId = ProductId(idGenerator())
        statement.execute(
            "INSERT INTO products (id, description, minimum_selling_price, currency) VALUES ('${productId.value}', '${product.description}', ${product.minimumSellingPrice.amount}, '${product.minimumSellingPrice.currency.currencyCode}');"
        )
        return productId
    }
}