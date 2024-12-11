package com.merchantsolutions.ports.users

import com.merchantsolutions.domain.User

interface Users<TX> {
    fun isValid(transactor: TX, token: String): Boolean
    fun getUserByToken(transactor: TX, token: String): User?
}