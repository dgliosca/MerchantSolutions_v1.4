package com.merchantsolutions.adapters.db

import com.merchantsolutions.domain.IdGenerator
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductId
import com.merchantsolutions.domain.ProductToRegister
import com.merchantsolutions.ports.Products
import java.math.BigDecimal
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.util.Currency
import java.util.UUID

class H2Products(val statement: Statement, val idGenerator: IdGenerator) : Products {

    override fun getProducts(): List<Product> {
        TODO("Not yet implemented")
    }

    override fun get(productId: ProductId): Product? {
        val selectSQL = "SELECT * FROM products WHERE id = '${productId.value}'";
        val rs = statement.executeQuery(selectSQL)
        return if (rs.next()) {
            val productId = ProductId(UUID.fromString(rs.getString("id")))
            val description = rs.getString("description")
            val monetaryAmount = rs.getBigDecimal("minimum_selling_price")
            val currency = Currency.getInstance(rs.getString("currency"))
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