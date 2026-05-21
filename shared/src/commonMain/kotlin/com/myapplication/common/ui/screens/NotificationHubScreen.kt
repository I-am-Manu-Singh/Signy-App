package com.myapplication.common.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.myapplication.common.ui.theme.TacticalTheme

class NotificationHubScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val notifications = remember {
            listOf(
                TacticalAlert("MOTION DETECTED", "FRONT GATE", "12:04 PM", true),
                TacticalAlert("SYSTEM UPDATE", "SECURE MESH", "10:45 AM", false),
                TacticalAlert("CONNECTED NODE", "ALPHA 1", "09:30 AM", true),
                TacticalAlert("DISCONNECTED NODE", "BRAVO 2", "08:15 AM", true)
            )
        }

        Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header (Status Bar Safety)
                Row(
                    modifier = Modifier.statusBarsPadding().fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TacticalTheme.colors.onBackground)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("NOTIFICATIONS", color = TacticalTheme.colors.onBackground, fontSize = 20.sp, fontWeight = FontWeight.Black)
                }

                // Alert Stream
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            "RECENT UPDATES", 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = TacticalTheme.extraColors.MutedSlate, 
                            letterSpacing = 1.sp
                        )
                    }

                    items(notifications) { alert ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            color = TacticalTheme.colors.surface.copy(alpha = 0.3f),
                            border = BorderStroke(1.dp, TacticalTheme.colors.onSurface.copy(alpha = 0.05f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = CircleShape,
                                    color = if (alert.isPriority) TacticalTheme.colors.error.copy(alpha = 0.1f) else Color(0xFF10B981).copy(alpha = 0.1f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            if (alert.isPriority) Icons.Default.PriorityHigh else Icons.Default.Info, 
                                            null, 
                                            tint = if (alert.isPriority) TacticalTheme.colors.error else Color(0xFF10B981), 
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(alert.title, color = TacticalTheme.colors.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(alert.description, color = TacticalTheme.extraColors.MutedSlate, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                                Spacer(Modifier.weight(1f))
                                Text(alert.time, fontSize = 10.sp, color = TacticalTheme.extraColors.MutedSlate.copy(alpha = 0.8f))
                            }
                        }
                    }
                }
            }
        }
    }
}

data class TacticalAlert(val title: String, val description: String, val time: String, val isPriority: Boolean)
