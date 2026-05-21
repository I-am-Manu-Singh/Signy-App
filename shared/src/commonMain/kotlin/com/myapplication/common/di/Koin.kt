package com.myapplication.common.di

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

import com.myapplication.common.data.supabase.SupabaseConfig
import com.myapplication.common.data.supabase.SupabaseService
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(networkModule, appModule)
    }

fun initKoin() = initKoin { }

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { 
                    ignoreUnknownKeys = true 
                    prettyPrint = true
                })
            }
            install(WebSockets)
        }
    }
}

val appModule = module {
    single { SupabaseConfig.client }
    single { SupabaseService(get()) }
    single { 
        com.myapplication.common.domain.webrtc.WebRTCManager(
            supabase = get(),
            scope = kotlinx.coroutines.MainScope()
        )
    }
}
