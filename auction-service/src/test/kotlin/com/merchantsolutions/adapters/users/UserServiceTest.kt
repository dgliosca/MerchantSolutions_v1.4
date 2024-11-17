package com.merchantsolutions.adapters.users

import com.merchantsolutions.drivers.http.UserApi
import com.merchantsolutions.ports.Users
import org.http4k.core.HttpHandler
import org.http4k.core.Uri

class UserServiceTest : UserContract {
    private val usersService: HttpHandler = UserApi()
    override val users: Users = UsersClient(Uri.of("http://user-service"), usersService)
}