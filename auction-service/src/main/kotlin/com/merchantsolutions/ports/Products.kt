package com.merchantsolutions.ports

import com.merchantsolutions.domain.Product
import com.merchantsolutions.domain.ProductId
import com.merchantsolutions.domain.ProductToRegister

interface Products<TX> {
    fun getProducts(transactor: TX) : List<Product>
    fun get(transactor: TX, productId: ProductId) : Product?
    fun add(transactor: TX, product: ProductToRegister) : ProductId
}