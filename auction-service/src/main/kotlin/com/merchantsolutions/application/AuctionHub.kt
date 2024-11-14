package com.merchantsolutions.application

import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductToRegister
import java.util.*


class AuctionHub {
    private val products = mutableListOf<Product>()

    fun add(product: ProductToRegister) {
        products.add(Product(UUID.randomUUID(), product.description))
    }

    fun listProducts(): List<Product> = products

}
