package com.merchantsolutions.domain

import java.math.BigDecimal
import java.math.BigInteger
import java.util.Currency

class Money(val currency: Currency, val amount: BigDecimal) {
    init {
        require(amount >= BigDecimal.ZERO) { "Amount must be non-negative" }
    }
    companion object {
        val usd = Currency.getInstance("USD")
        val gbp = Currency.getInstance("GBP")
        val euro = Currency.getInstance("EUR")
    }
}