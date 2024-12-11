package com.merchantsolutions.db

import java.sql.Connection
import java.sql.Statement

class H2Transactor(private val connection: Connection) : Transactor<H2TxContext> {
    init {
        connection.apply {
            autoCommit = false
        }
    }

    private val statement = connection.createStatement()

    override fun <T> invoke(unitOfWork: (H2TxContext) -> T): T {
        try {
            val result = unitOfWork(H2TxContext(statement))
            connection.commit()
            return result
        } catch (e: Exception) {
            connection.rollback()
            throw e
        }
    }
}

class H2TxContext(private val ctx: Statement) : TxContext, Statement by ctx

