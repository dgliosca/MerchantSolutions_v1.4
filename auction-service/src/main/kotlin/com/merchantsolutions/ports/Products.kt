package com.merchantsolutions.ports

import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductId
import com.merchantsolutions.domain.ProductToRegister

interface Products {
    fun getProducts() : List<Product>
    fun get(productId: ProductId) : Product?
    fun add(product: ProductToRegister) : ProductId
}