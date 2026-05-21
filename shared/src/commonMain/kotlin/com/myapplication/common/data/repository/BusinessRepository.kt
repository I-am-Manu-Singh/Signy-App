package com.myapplication.common.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

import com.myapplication.common.domain.model.BusinessLocation
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BusinessRepository(private val client: HttpClient) {
    private val baseUrl = "http://10.0.2.2:8080" // Emulator IP for localhost

    suspend fun registerBusiness(name: String, location: String, category: String): BusinessLocation {
        return client.post("$baseUrl/api/business") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("name" to name, "loc" to location, "category" to category))
        }.body()
    }

    fun getAllBusinesses(): Flow<List<BusinessLocation>> = flow {
        while (true) {
            val response: List<BusinessLocation> = client.get("$baseUrl/api/business").body()
            emit(response)
            kotlinx.coroutines.delay(5000) // Polling for demo, ideally use WebSockets
        }
    }
}
