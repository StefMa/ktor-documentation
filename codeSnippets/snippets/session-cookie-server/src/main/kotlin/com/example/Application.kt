package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*

data class CartSession(val userID: String, val productIDs: MutableList<Int>)

fun Application.main() {
    install(Sessions) {
        val secretSignKey = hex("6819b57a326945c1968f45236589")
        cookie<CartSession>("cart_session", SessionStorageMemory()) {
            cookie.path = "/"
            transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
        }
    }
    routing {
        get("/login") {
            call.sessions.set(CartSession(userID = "123", productIDs = mutableListOf(1, 3, 7)))
            call.respondRedirect("/cart")
        }

        get("/cart") {
            val cartSession = call.sessions.get<CartSession>()
            if (cartSession != null) {
                call.respondText("Product IDs: ${cartSession.productIDs}")
            } else {
                call.respondText("Your basket is empty.")
            }
        }

        get("/logout") {
            call.sessions.clear<CartSession>()
            call.respondRedirect("/cart")
        }
    }
}
