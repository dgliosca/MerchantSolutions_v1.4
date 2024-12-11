package com.merchantsolutions.adapters.db

import com.merchantsolutions.db.H2TxContext
import com.merchantsolutions.domain.User
import com.merchantsolutions.domain.UserId
import com.merchantsolutions.ports.users.Users
import java.util.UUID

class H2Users() : Users<H2TxContext> {

    override fun isValid(h2Context: H2TxContext, token: String): Boolean {
        return h2Context.executeQuery("SELECT user_id FROM token_to_user WHERE token = '$token'").next()
    }

    override fun getUserByToken(h2Context: H2TxContext, token: String): User? {
        val result = h2Context.executeQuery("SELECT u.id as user_id FROM users u JOIN token_to_user ttu ON u.id = ttu.user_id WHERE ttu.token = '$token'")
        return if (result.next()) {
            User(UserId(UUID.fromString(result.getString("user_id"))))
        } else
            null
    }
}