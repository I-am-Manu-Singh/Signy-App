package com.myapplication.common.ui.tabs

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.myapplication.common.ui.screens.SettingsScreen
import com.myapplication.common.ui.screens.EditProfileScreen
import com.myapplication.common.ui.components.*
import com.myapplication.common.ui.theme.TacticalTheme
import com.myapplication.common.data.supabase.SupabaseService
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

object ProfileTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Person)
            return remember {
                TabOptions(
                    index = 3u,
                    title = "Profile",
                    icon = icon
                )
            }
        }

    @OptIn(androidx.compose.material.ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
        val supabase = koinInject<SupabaseService>()
        val scope = rememberCoroutineScope()
        
        val user = supabase.client.auth.currentUserOrNull()

        Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background)) {
             // Aura
            Box(
                modifier = Modifier
                    .size(500.dp)
                    .align(Alignment.Center)
                    .alpha(0.05f)
                    .blur(100.dp)
                    .background(Brush.radialGradient(listOf(TacticalTheme.colors.primary, Color.Transparent)), CircleShape)
            )

            Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tactical Identity Header (Now Scrollable)
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            color = TacticalTheme.colors.surface.copy(alpha = 0.5f),
                            border = BorderStroke(2.dp, TacticalTheme.colors.primary.copy(alpha = 0.3f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = (user?.email ?: "U").take(1).uppercase(),
                                    style = TacticalTheme.typography.h1,
                                    color = TacticalTheme.colors.primary,
                                    fontSize = 48.sp
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(24.dp))
                        
                        Text(
                            text = user?.email ?: "TACTICAL_NODE_PENDING", 
                            color = TacticalTheme.colors.onBackground, 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "AUTHORIZED OPERATOR", 
                            color = TacticalTheme.colors.primary, 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                item {
                    TacticalButton(
                        text = "EDIT PROFILE",
                        onClick = { rootNavigator.push(EditProfileScreen()) },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp)
                    )
                }

                item { Spacer(Modifier.height(16.dp)) }

                item {
                    TacticalCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("COMMUNICATION BIO", color = TacticalTheme.colors.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Tactical communication specialist and secure network operator. Enthusiast in deep space telemetry and encrypted uplinks.",
                                color = TacticalTheme.colors.onSurface.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                item {
                     Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                         Box(modifier = Modifier.weight(1f)) {
                             StatCard("LOCATION", "MARS SECTOR 4", Icons.Default.LocationOn)
                         }
                         Box(modifier = Modifier.weight(1f)) {
                             StatCard("JOINED", "OCT 2024", Icons.Default.CalendarToday)
                         }
                     }
                }

                item { Spacer(Modifier.height(16.dp)) }
                item { Text("SECURITY UPLINKS", color = TacticalTheme.colors.primary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp) }
                
                item { ProfileItem("Linked Devices (3)", Icons.Default.Devices, TacticalTheme.colors.primary) { } }
                item { ProfileItem("Two-Factor Auth", Icons.Default.VpnKey, Color(0xFF10B981)) { } }
                
                item { Spacer(Modifier.height(16.dp)) }
                item { Text("PREFERENCES", color = TacticalTheme.colors.primary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp) }
                
                item { ProfileItem("Usage Analytics", Icons.Default.BarChart, Color(0xFFF472B6)) { } }
                item { ProfileItem("App Version 4.5.1", Icons.Default.Update, TacticalTheme.colors.onSurface.copy(alpha = 0.4f)) { } }
                
                item { Spacer(Modifier.height(24.dp)) }
                
                item {
                    TacticalButton(
                        text = "LOGOUT SESSION",
                        isPrimary = false,
                        onClick = { scope.launch { supabase.signOut(); rootNavigator.replaceAll(com.myapplication.common.ui.screens.AuthScreen) } },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    TacticalCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = TacticalTheme.colors.primary, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(8.dp))
                Text(label, color = TacticalTheme.extraColors.MutedSlate, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(value, color = TacticalTheme.colors.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    TacticalCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(12.dp), color = color.copy(alpha = 0.1f)) {
                 Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(20.dp)) }
            }
            Spacer(Modifier.width(16.dp))
            Text(label, modifier = Modifier.weight(1f), color = TacticalTheme.colors.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TacticalTheme.extraColors.MutedSlate, modifier = Modifier.size(20.dp))
        }
    }
}
