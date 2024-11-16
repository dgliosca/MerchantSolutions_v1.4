package com.merchantsolutions.domain

import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.BigInteger
import java.util.Currency

data class Money(val currency: Currency, val amount: BigDecimal) : Comparable<Money> {

    init {
        require(value = amount >= ZERO) { "Amount must be non-negative" }
    }

    override fun compareTo(other: Money): Int {
        checkCurrencyMatch(other);
        return this.amount.compareTo(other.amount);
    }

    private fun checkCurrencyMatch(other: Money) {
        if (this.currency != other.currency) {
            throw IllegalArgumentException ("Cannot compare or operate on Money with different currencies")
        }
    }

    companion object {
        val usd = Currency.getInstance("USD")
        val gbp = Currency.getInstance("GBP")
        val euro = Currency.getInstance("EUR")
    }
}