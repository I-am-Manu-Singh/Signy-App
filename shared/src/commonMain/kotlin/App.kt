package com.myapplication.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.myapplication.common.data.supabase.SupabaseService
import com.myapplication.common.ui.screens.AuthScreen
import com.myapplication.common.ui.screens.DashboardScreen
import com.myapplication.common.ui.theme.AppTheme
import com.myapplication.common.ui.theme.LocalThemeIsDark
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinContext {
        val darkThemeState = remember { mutableStateOf(true) }
        CompositionLocalProvider(LocalThemeIsDark provides darkThemeState) {
            AppTheme {
                val supabase = koinInject<SupabaseService>()
                val sessionStatus by supabase.client.auth.sessionStatus.collectAsState(
                    SessionStatus.NotAuthenticated(isSignOut = false)
                )

                LaunchedEffect(sessionStatus) {
                    if (sessionStatus is SessionStatus.Authenticated) {
                        supabase.syncUserProfile()
                    }
                }

                val initialScreen = remember(sessionStatus) {
                    if (sessionStatus is SessionStatus.Authenticated) DashboardScreen else AuthScreen
                }

                Box(modifier = Modifier.fillMaxSize().background(com.myapplication.common.ui.theme.TacticalTheme.colors.background)) {
                    Navigator(initialScreen) { navigator -> 
                        LaunchedEffect(sessionStatus) {
                            if (sessionStatus is SessionStatus.Authenticated) {
                                navigator.replaceAll(DashboardScreen)
                            } else if (sessionStatus is SessionStatus.NotAuthenticated && navigator.lastItem != AuthScreen) {
                                navigator.replaceAll(AuthScreen)
                            }
                        }
                        SlideTransition(navigator) 
                    }
                }
            }
        }
    }
}
