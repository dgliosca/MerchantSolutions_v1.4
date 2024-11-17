package com.merchantsolutions.application

import com.merchantsolutions.domain.User
import com.merchantsolutions.domain.UserId

class UserHub {
    fun isValid(token: String): Boolean {
        return true
    }

    fun getUserByToken(string: String): User {
        return User(UserId.of("3d02036f-4087-46e4-8a30-39d234d61de3"))
    }
}