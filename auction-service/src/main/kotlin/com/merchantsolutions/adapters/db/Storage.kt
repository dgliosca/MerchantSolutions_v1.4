package com.merchantsolutions.adapters.db

import java.lang.AutoCloseable
import java.sql.Statement

interface Storage : AutoCloseable {
    val statement: Statement
}