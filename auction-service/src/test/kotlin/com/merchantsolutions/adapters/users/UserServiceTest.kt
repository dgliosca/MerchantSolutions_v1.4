package com.merchantsolutions.adapters.users

import com.merchantsolutions.ports.Users
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled("have to implement user service first")
class UserServiceTest : UserContract {
    private val usersService : HttpHandler = { Response(OK) }
    override val users: Users = UsersClient(Uri.of("http://user-service"), usersService)
}