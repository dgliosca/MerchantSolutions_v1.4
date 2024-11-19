package com.merchantsolutions.adapters.db

import com.merchantsolutions.domain.User
import com.merchantsolutions.domain.UserId
import com.merchantsolutions.ports.Users
import java.sql.Statement
import java.util.UUID

class H2Users(val statement: Statement) : Users {

    override fun isValid(token: String): Boolean {
        return statement.executeQuery("SELECT user_id FROM token_to_user WHERE token = '$token'").next()
    }

    override fun getUserByToken(token: String): User? {
        val result = statement.executeQuery("SELECT u.id as user_id FROM users u JOIN token_to_user ttu ON u.id = ttu.user_id WHERE ttu.token = '$token'")
        return if (result.next()) {
            User(UserId(UUID.fromString(result.getString("user_id"))))
        } else
            null
    }
}