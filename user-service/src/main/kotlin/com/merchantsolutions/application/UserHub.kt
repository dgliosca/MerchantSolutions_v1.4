package com.merchantsolutions.application

import com.merchantsolutions.domain.User
import com.merchantsolutions.ports.Users

class UserHub(val users: Users) {

    fun isValid(token: String): Boolean {
        return users.isValid(token)
    }

    fun getUserByToken(token: String): User? {
        return users.getUserByToken(token)
    }
}