package com.myapplication.common.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.myapplication.common.ui.components.*
import com.myapplication.common.ui.theme.TacticalTheme

class DialerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var dialledNumber by remember { mutableStateOf("") }
        var selectedTab by remember { mutableStateOf("DIALER") } // DIALER or RECENT
        var inputMode by remember { mutableStateOf("NUMERIC") } // NUMERIC or EMAIL

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
                         Spacer(Modifier.weight(1f))
                         Text("MAKE A CALL", color = TacticalTheme.colors.onBackground, style = TacticalTheme.typography.h2)
                         Spacer(Modifier.weight(1f))
                         IconButton(onClick = { 
                             inputMode = if(inputMode == "NUMERIC") "EMAIL" else "NUMERIC" 
                             dialledNumber = ""
                         }) {
                             Icon(if(inputMode == "NUMERIC") Icons.Default.AlternateEmail else Icons.Default.Dialpad, null, tint = TacticalTheme.colors.primary)
                         }
                    }
                }

                // Tab Switcher
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.Center) {
                    Surface(
                        modifier = Modifier.height(40.dp).width(200.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = TacticalTheme.colors.surface,
                        border = BorderStroke(1.dp, TacticalTheme.colors.onSurface.copy(alpha = 0.05f))
                    ) {
                        Row {
                            TabItem("DIALER", selectedTab == "DIALER") { selectedTab = "DIALER" }
                            TabItem("RECENT", selectedTab == "RECENT") { selectedTab = "RECENT" }
                        }
                    }
                }

                if (selectedTab == "DIALER") {
                    DialerContent(dialledNumber, inputMode, onNumberUpdate = { dialledNumber = it }, onCall = { isVideo ->
                        if (dialledNumber.isNotEmpty()) {
                            navigator.push(ActiveCallScreen(dialledNumber, isVideo))
                        }
                    })
                } else {
                    RecentCallsContent { recent ->
                        dialledNumber = recent
                        selectedTab = "DIALER"
                    }
                }
            }
        }
    }

    @Composable
    private fun RowScope.TabItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(if (isSelected) TacticalTheme.colors.primary.copy(alpha = 0.1f) else Color.Transparent)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(label, color = if(isSelected) TacticalTheme.colors.onPrimary else TacticalTheme.extraColors.MutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }

    @Composable
    private fun DialerContent(number: String, inputMode: String, onNumberUpdate: (String) -> Unit, onCall: (Boolean) -> Unit) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            // Display Area
            Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                if (inputMode == "NUMERIC") {
                    Text(
                        text = number.ifEmpty { "000 000 000" },
                        fontSize = if (number.isEmpty()) 24.sp else 36.sp,
                        color = if (number.isEmpty()) TacticalTheme.extraColors.MutedSlate.copy(alpha = 0.3f) else TacticalTheme.colors.onSurface,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                } else {
                    androidx.compose.foundation.text.BasicTextField(
                        value = number,
                        onValueChange = onNumberUpdate,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = TacticalTheme.colors.onSurface, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(TacticalTheme.colors.primary),
                        decorationBox = { innerTextField ->
                            if (number.isEmpty()) Text("Enter Email/ID", color = TacticalTheme.extraColors.MutedSlate.copy(alpha = 0.5f), fontSize = 20.sp, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            innerTextField()
                        }
                    )
                }
                
                if (number.isNotEmpty()) {
                    IconButton(
                        onClick = { onNumberUpdate(number.dropLast(1)) },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Backspace, null, tint = TacticalTheme.extraColors.MutedSlate, modifier = Modifier.size(24.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Keypad
            if (inputMode == "NUMERIC") {
                val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#")
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    for (i in 0 until 4) {
                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            for (j in 0 until 3) {
                                val key = keys[i * 3 + j]
                                KeypadButton(key) { onNumberUpdate(number + key) }
                            }
                        }
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    shape = TacticalTheme.shapes.medium,
                    color = TacticalTheme.colors.surface.copy(alpha = 0.3f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(24.dp)) {
                        Text("Keyboard enabled for address entry", color = TacticalTheme.extraColors.MutedSlate, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // Call Actions
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Surface(
                    modifier = Modifier.size(72.dp).clickable { onCall(false) },
                    shape = CircleShape,
                    color = Color(0xFF10B981),
                    elevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Call, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }

                Spacer(Modifier.width(48.dp))

                Surface(
                    modifier = Modifier.size(72.dp).clickable { onCall(true) },
                    shape = CircleShape,
                    color = TacticalTheme.colors.primary,
                    elevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Videocam, null, tint = TacticalTheme.colors.onPrimary, modifier = Modifier.size(28.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun KeypadButton(text: String, onClick: () -> Unit) {
        Surface(
            modifier = Modifier.size(72.dp).clickable { onClick() },
            shape = CircleShape,
            color = TacticalTheme.colors.surface,
            border = BorderStroke(1.dp, TacticalTheme.colors.onSurface.copy(alpha = 0.05f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text, color = TacticalTheme.colors.onSurface, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    @Composable
    private fun RecentCallsContent(onSelect: (String) -> Unit) {
        val recents = listOf(
            RecentCall("Operator Alpha", "10:30 AM", "incoming"),
            RecentCall("Beta Node", "Yesterday", "outgoing"),
            RecentCall("Gamma Server", "Oct 18", "missed"),
            RecentCall("Delta Unit", "Oct 17", "incoming")
        )
        
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp)) {
            items(recents) { call ->
                TacticalCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onSelect(call.name) }
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                         TacticalAvatar(name = call.name, size = 44.dp)
                         Spacer(Modifier.width(16.dp))
                         Column(modifier = Modifier.weight(1f)) {
                             Text(call.name, color = TacticalTheme.colors.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                             Row(verticalAlignment = Alignment.CenterVertically) {
                                 Icon(
                                     imageVector = when(call.type) {
                                         "incoming" -> Icons.AutoMirrored.Filled.CallReceived
                                         "outgoing" -> Icons.AutoMirrored.Filled.CallMade
                                         else -> Icons.Default.CallMissed
                                     },
                                     contentDescription = null,
                                     tint = when(call.type) {
                                         "incoming" -> Color(0xFF10B981)
                                         "missed" -> TacticalTheme.colors.error
                                         else -> TacticalTheme.colors.primary
                                     },
                                     modifier = Modifier.size(12.dp)
                                 )
                                 Spacer(Modifier.width(4.dp))
                                 Text(call.time, color = TacticalTheme.extraColors.MutedSlate, fontSize = 10.sp)
                             }
                         }
                         IconButton(onClick = { onSelect(call.name) }) {
                             Icon(Icons.Default.Info, null, tint = TacticalTheme.extraColors.MutedSlate.copy(alpha = 0.4f))
                         }
                    }
                }
            }
        }
    }
}

data class RecentCall(val name: String, val time: String, val type: String)

