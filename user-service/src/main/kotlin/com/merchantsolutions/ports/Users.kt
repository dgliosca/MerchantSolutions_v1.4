package com.merchantsolutions.ports

import com.merchantsolutions.domain.User

interface Users {
    fun isValid(token: String): Boolean
    fun getUserByToken(token: String): User?
}