package com.merchantsolutions.adapters.users

import com.merchantsolutions.domain.User
import com.merchantsolutions.ports.Users
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Uri
import com.merchantsolutions.AuctionJson.auto
import com.merchantsolutions.AuctionJson.json
import org.http4k.core.Method.GET
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ClientFilters

class UsersClient(uri: Uri, httpHandler: HttpHandler) : Users {
    private val http = ClientFilters.SetBaseUriFrom(uri).then(httpHandler)

    override fun isValid(token: String): Boolean {
        val response = http(Request(GET, "/is-valid").with(Body.auto<String>().toLens() of token))
        return response.json<Boolean>()
    }

    override fun getUserByToken(token: String): User? {
        val response = http(Request(GET, "/user-by-token").with(Body.auto<String>().toLens() of token))
        return response.json<User>()
    }
}