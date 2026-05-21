package com.myapplication

import com.myapplication.common.util.MainView
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.myapplication.common.di.initKoin
import org.koin.android.ext.koin.androidContext

import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val supabaseClient: SupabaseClient by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        handleIntent(intent)
        
        val window = this.window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        setContent {
            MainView()
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Essential for SingleTask updates
        handleIntent(intent)
    }

    private fun handleIntent(intent: android.content.Intent?) {
        intent?.data?.let { uri ->
            android.util.Log.d("SIGNY_AUTH", "Extracted Identity Token Trace: $uri")
            
            lifecycleScope.launch {
                try {
                    supabaseClient.handleDeeplinks(intent)
                    
                    // Critical Stabilization Window
                    delay(800) 
                    
                    val session = supabaseClient.auth.currentSessionOrNull()
                    android.util.Log.d("SIGNY_AUTH", "Identity Handshake Status: ${if (session != null) "SUCCESS" else "FAILED"}")
                    
                    if (session != null) {
                        // Forcing identity sync on successful handshake
                        com.myapplication.common.data.supabase.SupabaseService(supabaseClient).syncUserProfile()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("SIGNY_AUTH", "Handshake Error: ${e.message}")
                }
            }
        }
    }
}