package com.myapplication.common.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.myapplication.common.data.supabase.SupabaseService
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

object PhoneAuthScreen : Screen {
    @Composable
    override fun Content() {
        var phoneNumber by remember { mutableStateOf("") }
        var otpCode by remember { mutableStateOf("") }
        var isOtpSent by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val supabase = koinInject<SupabaseService>()

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020617))) {
            // High-Fidelity Glow Matrix
            Box(
                    modifier =
                            Modifier.size(400.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 100.dp, y = (-100).dp)
                                    .alpha(0.1f)
                                    .blur(80.dp)
                                    .background(
                                            Brush.radialGradient(
                                                    listOf(Color(0xFF22D3EE), Color.Transparent)
                                            ),
                                            CircleShape
                                    )
            )

            Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(60.dp))

                // Tactical Back Handshake
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    IconButton(
                            onClick = { if (isOtpSent) isOtpSent = false else navigator.pop() }
                    ) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                }

                Spacer(Modifier.height(32.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                            if (isOtpSent) "IDENTITY VERIFIED" else "PHONE COMMAND",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                    )
                    Text(
                            if (isOtpSent) "ESTABLISHING SECURE HANDSHAKE..."
                            else "ESTABLISH SECURE TACTICAL UPLINK",
                            color = Color(0xFF22D3EE),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(64.dp))

                Surface(
                        shape = RoundedCornerShape(32.dp),
                        color = Color(0xFF1E293B).copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                        modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        if (!isOtpSent) {
                            OutlinedTextField(
                                    value = phoneNumber,
                                    onValueChange = { phoneNumber = it },
                                    label = {
                                        Text(
                                                "Enter Mobile No (e.g. +91...)",
                                                color = Color(0xFF64748B)
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    leadingIcon = {
                                        Icon(
                                                Icons.Default.Phone,
                                                null,
                                                tint = Color(0xFF22D3EE),
                                                modifier = Modifier.padding(start = 12.dp)
                                        )
                                    },
                                    colors =
                                            TextFieldDefaults.outlinedTextFieldColors(
                                                    textColor = Color.White,
                                                    focusedBorderColor = Color(0xFF22D3EE),
                                                    unfocusedBorderColor =
                                                            Color.White.copy(alpha = 0.1f)
                                            )
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                    onClick = {
                                        if (phoneNumber.isNotEmpty()) {
                                            isLoading = true
                                            scope.launch {
                                                try {
                                                    supabase.signInWithPhone(phoneNumber)
                                                    isOtpSent = true
                                                } catch (e: Exception) {
                                                    errorMessage = "Handshake Error: ${e.message}"
                                                    isOtpSent =
                                                            true // Keep fallback active for testing
                                                } finally {
                                                    isLoading = false
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(64.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors =
                                            ButtonDefaults.buttonColors(
                                                    backgroundColor = Color(0xFF22D3EE)
                                            ),
                                    enabled = !isLoading
                            ) {
                                if (isLoading) CircularProgressIndicator(color = Color.Black)
                                else
                                        Text(
                                                "SEND CODE",
                                                color = Color.Black,
                                                fontWeight = FontWeight.Black
                                        )
                            }
                        } else {
                            OutlinedTextField(
                                    value = otpCode,
                                    onValueChange = {
                                        if (it.all { char -> char.isDigit() } && it.length <= 6)
                                                otpCode = it
                                    },
                                    label = {
                                        Text(
                                                "Enter 6-Digit Code for $phoneNumber",
                                                color = Color(0xFF64748B)
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors =
                                            TextFieldDefaults.outlinedTextFieldColors(
                                                    textColor = Color.White,
                                                    focusedBorderColor = Color(0xFF10B981),
                                                    unfocusedBorderColor =
                                                            Color.White.copy(alpha = 0.1f)
                                            )
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                    onClick = {
                                        if (otpCode.length == 6) {
                                            isLoading = true
                                            errorMessage = null
                                            scope.launch {
                                                try {
                                                    supabase.verifyOtp(phoneNumber, otpCode)
                                                    if (supabase.isLoggedIn()) {
                                                        supabase.syncUserProfile()
                                                        navigator.replaceAll(DashboardScreen)
                                                    } else {
                                                        errorMessage = "Uplink Failed: Session Null"
                                                    }
                                                } catch (e: Exception) {
                                                    errorMessage =
                                                            "Verification Protocol Error: ${e.message}"
                                                } finally {
                                                    isLoading = false
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(64.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors =
                                            ButtonDefaults.buttonColors(
                                                    backgroundColor = Color(0xFF10B981)
                                            ),
                                    enabled = !isLoading
                            ) {
                                if (isLoading) CircularProgressIndicator(color = Color.Black)
                                else
                                        Text(
                                                "VERIFY CODE",
                                                color = Color.Black,
                                                fontWeight = FontWeight.Black
                                        )
                            }
                        }

                        if (errorMessage != null) {
                            Text(
                                    errorMessage!!,
                                    color = Color(0xFFEF4444),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier =
                                            Modifier.padding(top = 16.dp)
                                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        }
    }
}
