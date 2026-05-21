package com.myapplication.common.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.myapplication.common.data.supabase.SupabaseService
import com.myapplication.common.ui.components.*
import com.myapplication.common.ui.theme.TacticalTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

object AuthScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val supabase = koinInject<SupabaseService>()
        val scope = rememberCoroutineScope()
        
        Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background)) {
            // Atmospheric Foundation
            Box(
                modifier = Modifier
                    .size(600.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 300.dp)
                    .alpha(0.12f)
                    .blur(100.dp)
                    .background(Brush.radialGradient(listOf(TacticalTheme.colors.primary, Color.Transparent)), CircleShape)
            )

            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Tactical Core Icon
                Surface(
                    modifier = Modifier.size(110.dp),
                    shape = TacticalTheme.shapes.large,
                    color = TacticalTheme.colors.surface,
                    border = androidx.compose.foundation.BorderStroke(2.dp, TacticalTheme.colors.primary.copy(alpha = 0.2f)),
                    elevation = 20.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Security, 
                            contentDescription = null, 
                            tint = TacticalTheme.colors.primary,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }
                
                Spacer(Modifier.height(40.dp))
                
                Text(
                    text = "WELCOME BACK", 
                    style = TacticalTheme.typography.h1,
                    color = TacticalTheme.colors.onBackground,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Manage your home with ease", 
                    color = TacticalTheme.colors.primary, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                    letterSpacing = 1.sp
                )
                
                Spacer(Modifier.height(80.dp))
                
                // Auth Actions
                TacticalButton(
                    text = "Login with Google",
                    onClick = {
                        scope.launch {
                            try {
                                supabase.signInWithGoogle()
                            } catch (e: Exception) {
                                // Handled via MainActivity loop
                            }
                        }
                    }
                )
                
                Spacer(Modifier.height(16.dp))
                
                TacticalButton(
                    text = "Login with Email",
                    isPrimary = false,
                    onClick = { navigator.push(EmailAuthScreen) }
                )
                
                Spacer(Modifier.height(16.dp))
                
                TacticalButton(
                    text = "Login with Phone",
                    isPrimary = false,
                    onClick = { navigator.push(PhoneAuthScreen) }
                )
                
                Spacer(Modifier.height(56.dp))
                
                Text(
                    text = "By continuing, you agree to our Terms of Service", 
                    color = TacticalTheme.extraColors.MutedSlate, 
                    fontSize = 10.sp, 
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
