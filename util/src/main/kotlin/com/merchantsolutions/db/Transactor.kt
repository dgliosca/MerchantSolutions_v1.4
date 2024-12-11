package com.merchantsolutions.db


interface TxContext

data object NoTxContext : TxContext

interface Transactor<TX> {
    operator fun <T> invoke(unitOfWork: (TX) -> T): T
}