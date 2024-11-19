package com.merchantsolutions.adapters.db

import com.merchantsolutions.domain.User
import com.merchantsolutions.domain.UserId
import com.merchantsolutions.ports.Users
import java.sql.Statement
import java.util.UUID

class H2Users(val statement: Statement) : Users {

    override fun isValid(token: String): Boolean {
        val selectSQL = "SELECT user_id FROM token_to_user WHERE token = '$token'"
        val rs = statement.executeQuery(selectSQL)
        return rs.next()
    }

    override fun getUserByToken(token: String): User? {
        val selectSQL =
            "SELECT u.id as user_id FROM users u JOIN token_to_user ttu ON u.id = ttu.user_id WHERE ttu.token = '$token'"
        val rs = statement.executeQuery(selectSQL)
        return if (rs.next()) {
            User(UserId(UUID.fromString(rs.getString("user_id"))))
        } else
            null
    }
}