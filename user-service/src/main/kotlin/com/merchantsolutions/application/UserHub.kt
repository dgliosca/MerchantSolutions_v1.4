package com.merchantsolutions.application

import com.merchantsolutions.domain.User
import com.merchantsolutions.domain.UserId
import com.merchantsolutions.ports.Users

class UserHub(val users: Users) {
    fun isValid(token: String): Boolean {
        return users.isValid(token)
    }

    fun getUserByToken(string: String): User {
        return User(UserId.of("3d02036f-4087-46e4-8a30-39d234d61de3"))
    }
}