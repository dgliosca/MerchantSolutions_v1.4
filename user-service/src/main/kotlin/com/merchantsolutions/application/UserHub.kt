package com.merchantsolutions.application

import com.merchantsolutions.db.Transactor
import com.merchantsolutions.domain.User
import com.merchantsolutions.ports.users.Users

class UserHub<TX>(val users: Users<TX>, val transactor: Transactor<TX>) {

    fun isValid(token: String): Boolean {
        return transactor {
            users.isValid(it, token)
        }
    }

    fun getUserByToken(token: String): User? {
        return transactor {
            users.getUserByToken(it, token)
        }
    }
}