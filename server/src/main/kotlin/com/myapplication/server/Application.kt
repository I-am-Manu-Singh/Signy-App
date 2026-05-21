package com.myapplication.server

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Serializable
data class Business(val id: String, val name: String, val loc: String, val category: String)

val businesses = ConcurrentHashMap<String, Business>()
val sessions = ConcurrentHashMap<String, WebSocketServerSession>()

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json(Json { prettyPrint = true })
        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }

        routing {
            get("/") {
                call.respondText("Signy E2E Backend Active")
            }

            // Google Business Registry like API
            route("/api/business") {
                get {
                    call.respond(businesses.values)
                }
                post {
                    val biz = call.receive<Business>()
                    val id = UUID.randomUUID().toString()
                    val newBiz = biz.copy(id = id)
                    businesses[id] = newBiz
                    call.respond(newBiz)
                }
                get("/{id}") {
                    val id = call.parameters["id"]
                    businesses[id]?.let { call.respond(it) } ?: call.respondText("Not Found", status = io.ktor.http.HttpStatusCode.NotFound)
                }
            }

            // Real-time Messaging & WebRTC Signaling
            webSocket("/chat") {
                val sessionId = UUID.randomUUID().toString()
                sessions[sessionId] = this
                try {
                    for (frame in incoming) {
                        // Forward message to all sessions (or specific users)
                        for (session in sessions.values) {
                            session.send(frame.copy())
                        }
                    }
                } finally {
                    sessions.remove(sessionId)
                }
            }
        }
    }.start(wait = true)
}
