package com.myapplication.common.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseConfig {
    const val CLIENT_URL = "https://ycqrpkpfwpgshhgssfuc.supabase.co"
    const val CLIENT_KEY = "sb_publishable_LKPXbrpYpEZ9Yizr-4TZwA_L5ad0JQp"

    val client: SupabaseClient by lazy {
        try {
            createSupabaseClient(CLIENT_URL, CLIENT_KEY) {
                install(Auth) {
                    scheme = "signy"
                    host = "auth"
                }
                install(Postgrest)
                install(Realtime)
            }
        } catch (e: Exception) {
            println("Supabase Tactical Initialization Error: ${e.message}")
            throw e
        }
    }
}
