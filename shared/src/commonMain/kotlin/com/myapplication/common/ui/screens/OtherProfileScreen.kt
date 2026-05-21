package com.myapplication.common.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.myapplication.common.ui.components.*
import com.myapplication.common.ui.theme.TacticalTheme

class OtherProfileScreen(val userId: String, val name: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Surface(
                    modifier = Modifier.statusBarsPadding().fillMaxWidth().height(80.dp),
                    color = Color.Transparent
                ) {
                    Row(modifier = Modifier.padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navigator.pop() }) {
                             Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("USER PROFILE", color = Color.White, style = TacticalTheme.typography.h2)
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TacticalAvatar(name = name, size = 120.dp, fontSize = 48)
                    Spacer(Modifier.height(24.dp))
                    Text(name.uppercase(), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    Text("Available", color = TacticalTheme.colors.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Spacer(Modifier.height(48.dp))

                    // Social Actions
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        ProfileControl(Icons.Default.Chat, "MESSAGE") { navigator.pop() }
                        ProfileControl(Icons.Default.Phone, "VOICE") { navigator.push(ActiveCallScreen(name, false)) }
                        ProfileControl(Icons.Default.Videocam, "VIDEO") { navigator.push(ActiveCallScreen(name, true)) }
                    }

                    Spacer(Modifier.height(48.dp))
                    
                    // Profile Info
                    InfoItem("STATUS", "Online", Icons.Default.Info)
                    InfoItem("USERNAME", "@${name.lowercase().replace(" ", "_")}", Icons.Default.AlternateEmail)
                    InfoItem("USER ID", userId.take(12).uppercase(), Icons.Default.VpnKey)
                }
            }
        }
    }

    @Composable
    private fun ProfileControl(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(60.dp).clickable { onClick() },
                shape = CircleShape,
                color = TacticalTheme.colors.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = TacticalTheme.colors.primary, modifier = Modifier.size(24.dp)) }
            }
            Spacer(Modifier.height(10.dp))
            Text(label, color = TacticalTheme.extraColors.MutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }

    @Composable
    private fun InfoItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = TacticalTheme.colors.surface) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = TacticalTheme.extraColors.MutedSlate, modifier = Modifier.size(18.dp)) }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(label, color = TacticalTheme.extraColors.MutedSlate, fontSize = 9.sp, fontWeight = FontWeight.Black)
                Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
