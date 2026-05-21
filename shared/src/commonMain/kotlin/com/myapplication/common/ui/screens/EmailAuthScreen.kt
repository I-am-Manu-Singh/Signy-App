package com.myapplication.common.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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

object EmailAuthScreen : Screen {
    @Composable
    override fun Content() {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isSignUp by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val supabase = koinInject<SupabaseService>()

        Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background)) {
            // Background Dynamic Atmosphere
            Box(
                modifier = Modifier
                    .size(400.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 150.dp, y = (-100).dp)
                    .alpha(0.1f)
                    .blur(100.dp)
                    .background(Brush.radialGradient(listOf(TacticalTheme.colors.primary, Color.Transparent)), CircleShape)
            )

            Column(
                modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = { navigator.pop() },
                        modifier = Modifier.background(TacticalTheme.colors.surface.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TacticalTheme.colors.onBackground)
                    }
                }

                Spacer(Modifier.height(40.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isSignUp) "SIGN UP" else "LOGIN", 
                        style = TacticalTheme.typography.h1,
                        color = TacticalTheme.colors.onBackground,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (isSignUp) "Create your account" else "Access your account", 
                        color = TacticalTheme.colors.primary, 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp),
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(64.dp))

                TacticalCard {
                    TacticalTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        leadingIcon = Icons.Default.Email
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    TacticalTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    TacticalButton(
                        text = if (isSignUp) "Create Account" else "Login",
                        isLoading = isLoading,
                        onClick = {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                isLoading = true
                                errorMessage = null
                                scope.launch {
                                    try {
                                        if (isSignUp) {
                                            supabase.signUp(email, password)
                                            supabase.signIn(email, password)
                                        } else {
                                            supabase.signIn(email, password)
                                        }
                                        
                                        if (supabase.isLoggedIn()) {
                                            supabase.syncUserProfile()
                                            navigator.replaceAll(DashboardScreen)
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "LOGIN ERROR: ${e.message?.uppercase()}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        }
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Text(
                        text = if(isSignUp) "ALREADY HAVE AN ACCOUNT? LOGIN" else "NEW USER? CREATE ACCOUNT",
                        color = TacticalTheme.colors.primary.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { isSignUp = !isSignUp }.align(Alignment.CenterHorizontally),
                        letterSpacing = 0.5.sp
                    )

                    AnimatedVisibility(visible = errorMessage != null) {
                        Surface(
                            modifier = Modifier.padding(top = 24.dp).fillMaxWidth(),
                            color = TacticalTheme.colors.error.copy(alpha = 0.1f),
                            shape = TacticalTheme.shapes.small,
                            border = androidx.compose.foundation.BorderStroke(1.dp, TacticalTheme.colors.error.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = errorMessage ?: "", 
                                color = TacticalTheme.colors.error, 
                                fontSize = 10.sp, 
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
