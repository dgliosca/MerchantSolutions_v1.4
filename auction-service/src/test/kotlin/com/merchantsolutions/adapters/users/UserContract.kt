package com.merchantsolutions.adapters.users

import com.merchantsolutions.domain.User
import com.merchantsolutions.domain.UserId
import com.merchantsolutions.ports.Users
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

interface UserContract {
    val users : Users

    @Test
    fun `can get user by token`() {
        val user = users.getUserByToken("00000000-0000-0000-1111-000000000001")
        assertThat(user, equalTo(User(UserId.of("00000000-0000-0000-0000-000000000001"))))
    }

    @Test
    fun `is valid token`() {
        val valid = users.isValid("00000000-0000-0000-1111-000000000001")
        assertThat(valid, equalTo(true))
    }
}