package com.merchantsolutions.adapters

import com.merchantsolutions.domain.User
import com.merchantsolutions.domain.UserId
import com.merchantsolutions.ports.Users
import java.util.UUID

class InMemoryUsers : Users {
    val buyer = User(UserId(UUID.fromString("00000000-0000-0000-0000-000000000002")))
    val seller = User(UserId(UUID.fromString("00000000-0000-0000-0000-000000000005")))
    val backOffice = User(UserId(UUID.fromString("00000000-0000-0000-0000-000000000003")))
    private val users = listOf<User>(buyer)
    private val tokenToUsers = mutableMapOf<String, User>(
        "00000000-0000-0000-0000-000000000001" to buyer,
        "00000000-0000-0000-0000-000000000003" to backOffice,
        "00000000-0000-0000-0000-000000000005" to seller
    )

    override fun isValid(token: String) = tokenToUsers[token] != null
    override fun getUserByToken(token: String): User? {
        return tokenToUsers[token]
    }
}
