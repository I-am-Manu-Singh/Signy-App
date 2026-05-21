package com.myapplication.common

import androidx.compose.ui.window.ComposeUIViewController
import com.myapplication.common.di.initKoin

private var koinInitialized = false

fun MainViewController() = ComposeUIViewController {
    if (!koinInitialized) {
        try {
            initKoin()
            koinInitialized = true
        } catch (e: Exception) {
            // Handshake logic: already started or other non-critical boot failure
        }
    }
    App() 
}