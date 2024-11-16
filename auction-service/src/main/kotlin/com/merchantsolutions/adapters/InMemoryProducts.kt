package com.merchantsolutions.adapters

import com.merchantsolutions.domain.IdGenerator
import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductId
import com.merchantsolutions.domain.ProductId.Companion.of
import com.merchantsolutions.domain.ProductToRegister
import com.merchantsolutions.ports.Products

class InMemoryProducts(val idGenerator: IdGenerator) : Products {
    private val products = mutableListOf<Product>()

    override fun getProducts(): List<Product> = products

    override fun get(productId: ProductId): Product? {
        return products.find { it.productId == productId }
    }

    override fun add(product: ProductToRegister): ProductId {
        val productId = of(idGenerator())
        val product = Product(productId, product.description, product.minimumSellingPrice)
        products.add(product)
        return productId
    }
}