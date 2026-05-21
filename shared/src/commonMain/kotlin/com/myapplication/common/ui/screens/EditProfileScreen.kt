package com.myapplication.common.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.myapplication.common.ui.components.*
import com.myapplication.common.ui.theme.TacticalTheme
import kotlinx.serialization.json.put
import kotlinx.serialization.json.buildJsonObject
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class EditProfileScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val supabase = org.koin.compose.koinInject<com.myapplication.common.data.supabase.SupabaseService>()
        val user = supabase.auth.currentUserOrNull()
        
        var name by remember { mutableStateOf(user?.userMetadata?.get("full_name")?.toString()?.replace("\"", "") ?: "") }
        var phone by remember { mutableStateOf(user?.phone ?: "") }
        var bio by remember { mutableStateOf(user?.userMetadata?.get("bio")?.toString()?.replace("\"", "") ?: "") }
        var location by remember { mutableStateOf(user?.userMetadata?.get("location")?.toString()?.replace("\"", "") ?: "") }

        var isLoading by remember { mutableStateOf(false) }
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()

        Scaffold(
            scaffoldState = scaffoldState,
            backgroundColor = TacticalTheme.colors.background
        ) {
            Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header
                    Surface(
                        modifier = Modifier.statusBarsPadding().fillMaxWidth().height(64.dp),
                        color = Color.Transparent
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                             IconButton(onClick = { navigator.pop() }) {
                                 Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TacticalTheme.colors.onBackground)
                             }
                             Spacer(Modifier.width(12.dp))
                             Text("Edit Profile", color = TacticalTheme.colors.onBackground, style = TacticalTheme.typography.h2)
                        }
                    }

                    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        item { Spacer(Modifier.height(24.dp)) }
                        item {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                TacticalAvatar(name = if(name.isEmpty()) "User" else name, size = 120.dp, fontSize = 48)
                                FloatingActionButton(
                                    onClick = { /* Image Picker Placeholder */ },
                                    modifier = Modifier.size(40.dp),
                                    backgroundColor = TacticalTheme.colors.primary,
                                    contentColor = TacticalTheme.colors.onPrimary
                                ) {
                                    Icon(Icons.Default.PhotoCamera, null, modifier = Modifier.size(20.dp))
                                }
                            }
                        }

                        item { Spacer(Modifier.height(40.dp)) }

                        item {
                            TacticalInputField(label = "FULL NAME", value = name, onValueChange = { name = it }, icon = Icons.Default.Person)
                            Spacer(Modifier.height(20.dp))
                        }
                        item {
                            TacticalInputField(label = "PHONE NUMBER", value = phone, onValueChange = { phone = it }, icon = Icons.Default.Phone)
                            Spacer(Modifier.height(20.dp))
                        }
                        item {
                            TacticalInputField(label = "LOCATION", value = location, onValueChange = { location = it }, icon = Icons.Default.LocationOn)
                            Spacer(Modifier.height(20.dp))
                        }
                        item {
                             Column(modifier = Modifier.fillMaxWidth()) {
                                 Text("BIO", color = TacticalTheme.extraColors.MutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                                 Spacer(Modifier.height(8.dp))
                                 TacticalTextField(
                                     value = bio,
                                     onValueChange = { bio = it },
                                     modifier = Modifier.fillMaxWidth().height(100.dp),
                                     singleLine = false
                                 )
                             }
                             Spacer(Modifier.height(20.dp))
                        }
                        
                        item {
                            var password by remember { mutableStateOf("") }
                            var showPasswordChange by remember { mutableStateOf(false) }

                            TacticalCard(
                                modifier = Modifier.fillMaxWidth().clickable { showPasswordChange = !showPasswordChange }
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, null, tint = TacticalTheme.colors.primary)
                                    Spacer(Modifier.width(16.dp))
                                    Text("Change Password", color = TacticalTheme.colors.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    Icon(if(showPasswordChange) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = TacticalTheme.extraColors.MutedSlate)
                                }
                            }

                            if (showPasswordChange) {
                                Spacer(Modifier.height(16.dp))
                                TacticalInputField(
                                    label = "NEW PASSWORD", 
                                    value = password, 
                                    onValueChange = { password = it }, 
                                    icon = Icons.Default.Security,
                                    isPassword = true
                                )
                            }
                            Spacer(Modifier.height(40.dp))
                        }

                        item {
                            TacticalButton(
                                text = "SAVE CHANGES",
                                onClick = { 
                                    scope.launch {
                                        try {
                                            isLoading = true
                                            val metadata = buildJsonObject { 
                                                put("full_name", name)
                                                put("bio", bio)
                                                put("location", location)
                                            }
                                            supabase.auth.updateUser {
                                                data = metadata
                                                if (phone != (user?.phone ?: "")) {
                                                    this.phone = phone
                                                }
                                            }
                                            scaffoldState.snackbarHostState.showSnackbar("Profile updated successfully")
                                            delay(1500)
                                            navigator.pop()
                                        } catch (e: Exception) {
                                            scaffoldState.snackbarHostState.showSnackbar("Update failed: ${e.message}")
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                isLoading = isLoading
                            )
                            Spacer(Modifier.height(40.dp))
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun TacticalInputField(
        label: String, 
        value: String, 
        onValueChange: (String) -> Unit, 
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        isPassword: Boolean = false
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(label, color = TacticalTheme.extraColors.MutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.height(8.dp))
            TacticalTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                isPassword = isPassword,
                singleLine = true
            )
        }
    }
}

