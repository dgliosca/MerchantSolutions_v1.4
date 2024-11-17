package com.merchantsolutions.adapters.users

import com.merchantsolutions.ports.Users
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isNullOrBlank
import org.junit.jupiter.api.Test

interface UserContract {
    val users : Users

    @Test
    fun `can get user by token`() {
        val user = users.getUserByToken("123")
        assertThat(user, equalTo(!isNullOrBlank))
    }

    @Test
    fun `is valid token`() {
        val valid = users.isValid("123")
        assertThat(valid, equalTo(true))
    }
}