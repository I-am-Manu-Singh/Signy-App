package com.myapplication.common.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject
import kotlinx.coroutines.launch

class StartCallScreen : Screen {
    @Composable
    override fun Content() {
        var targetIdentity by remember { mutableStateOf("") }
        var currentMode by remember { mutableStateOf(0) } // 0: DIALER, 1: EMAIL, 2: MEETING
        val modes = listOf("DIALER", "EMAIL", "MEETING")
        val navigator = LocalNavigator.currentOrThrow
        val supabase = koinInject<com.myapplication.common.data.supabase.SupabaseService>()

        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF020617))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp).padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    IconButton(
                        onClick = { navigator.pop() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1E293B).copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                        ) {
                            Box(modifier = Modifier.padding(10.dp)) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                    
                    Text(
                        "SYSTEM ${modes[currentMode]}",
                        modifier = Modifier.align(Alignment.Center),
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = Color(0xFF94A3B8)
                        )
                    )

                    Surface(
                        modifier = Modifier.align(Alignment.CenterEnd).clickable { currentMode = (currentMode + 1) % 3 },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                         Text(
                            "SWITCH",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color(0xFF22D3EE),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                         )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(32.dp))
                    
                    // Display Area
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(28.dp),
                        color = Color(0xFF1E293B).copy(alpha = 0.3f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 24.dp)) {
                             if (currentMode > 0) {
                                 androidx.compose.foundation.text.BasicTextField(
                                     value = targetIdentity,
                                     onValueChange = { targetIdentity = it },
                                     textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                                     modifier = Modifier.fillMaxWidth(),
                                     cursorBrush = androidx.compose.ui.graphics.SolidColor(Color(0xFF22D3EE)),
                                     decorationBox = { inner ->
                                         if (targetIdentity.isEmpty()) {
                                             Text(
                                                 if (currentMode == 1) "NODE_ID@PROTOCOL.COM" else "ENTER_ROOM_ID", 
                                                 color = Color(0xFF475569), 
                                                 textAlign = TextAlign.Center, 
                                                 modifier = Modifier.fillMaxWidth(),
                                                 fontSize = 14.sp
                                             )
                                         }
                                         inner()
                                     }
                                 )
                             } else {
                                Text(
                                    targetIdentity.ifEmpty { "EXTENSION" },
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (targetIdentity.isEmpty()) Color(0xFF475569) else Color.White,
                                    letterSpacing = 2.sp
                                )
                             }
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    if (currentMode == 0) {
                        // Keypad
                        val keys = listOf(
                            DialKey("1", ""), DialKey("2", "ABC"), DialKey("3", "DEF"),
                            DialKey("4", "GHI"), DialKey("5", "JKL"), DialKey("6", "MNO"),
                            DialKey("7", "PQRS"), DialKey("8", "TUV"), DialKey("9", "WXYZ"),
                            DialKey("*", ""), DialKey("0", "+"), DialKey("#", "")
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier.width(300.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            items(keys) { key ->
                                Surface(
                                    modifier = Modifier.size(80.dp).clickable { 
                                        if (targetIdentity.length < 32) targetIdentity += key.num 
                                    },
                                    shape = CircleShape,
                                    color = Color(0xFF1E293B).copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(key.num, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        if (key.alph.isNotEmpty()) {
                                            Text(key.alph, fontSize = 8.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Email/Meeting Mode Info
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF22D3EE).copy(alpha = 0.05f),
                            border = BorderStroke(1.dp, Color(0xFF22D3EE).copy(alpha = 0.1f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, null, tint = Color(0xFF22D3EE), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    if (currentMode == 1) "ENTERING AN IDENTITY EMAIL WILL INITIATE A SECURE CONNECTION REQUEST PROTOCOL." 
                                    else "JOINING A ROOM ENABLE MULTI-PARTICIPANT GALLERY MESH DISCOVERY.", 
                                    color = Color(0xFF94A3B8), 
                                    fontSize = 9.sp, 
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 56.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InDialerAction(Icons.AutoMirrored.Filled.Backspace, Color.White.copy(alpha = 0.4f)) {
                            if (targetIdentity.isNotEmpty()) targetIdentity = targetIdentity.dropLast(1)
                        }

                        val scope = rememberCoroutineScope()
                        Surface(
                            modifier = Modifier.size(80.dp).clickable { 
                                if (targetIdentity.isNotEmpty()) {
                                    when (currentMode) {
                                        1 -> { // EMAIL
                                            if (targetIdentity.contains("@")) {
                                                scope.launch { supabase.sendConnectionRequest(targetIdentity) }
                                                navigator.pop()
                                            }
                                        }
                                        2 -> { // MEETING
                                            navigator.push(ActiveCallScreen(targetIdentity, true, meetingId = targetIdentity))
                                        }
                                        else -> { // DIALER
                                            navigator.push(ActiveCallScreen(targetIdentity, false))
                                        }
                                    }
                                }
                            },
                            shape = CircleShape,
                            color = if (currentMode == 2) Color(0xFF22D3EE) else Color(0xFF10B981),
                            elevation = 8.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    when (currentMode) {
                                        1 -> Icons.Default.Send
                                        2 -> Icons.Default.MeetingRoom
                                        else -> Icons.Default.Call
                                    }, 
                                    null, tint = Color.Black, modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        if (currentMode == 0) {
                            InDialerAction(Icons.Default.Videocam, Color(0xFF22D3EE)) {
                                if (targetIdentity.isNotEmpty()) navigator.push(ActiveCallScreen(targetIdentity, true))
                            }
                        } else {
                             InDialerAction(Icons.Default.History, Color.White.copy(alpha = 0.4f)) {
                                 // REJOIN RECENT SIM
                             }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InDialerAction(icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(56.dp).clickable { onClick() },
        shape = CircleShape,
        color = Color(0xFF1E293B).copy(alpha = 0.5f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
        }
    }
}

data class DialKey(val num: String, val alph: String)
