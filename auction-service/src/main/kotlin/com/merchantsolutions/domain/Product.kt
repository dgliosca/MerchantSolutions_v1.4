package com.merchantsolutions.domain

import java.util.UUID

data class Product(val id: UUID, val description: String, val minimumSellingPrice: Money)