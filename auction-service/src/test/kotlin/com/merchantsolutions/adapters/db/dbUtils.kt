package com.merchantsolutions.adapters.db

fun Storage.truncateTables() {
    statement.execute("TRUNCATE TABLE AUCTIONS")
    statement.execute("TRUNCATE TABLE PRODUCTS")
    statement.execute("TRUNCATE TABLE BIDS")
}
