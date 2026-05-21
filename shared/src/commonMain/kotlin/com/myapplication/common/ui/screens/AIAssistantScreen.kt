package com.myapplication.common.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import com.myapplication.common.ui.components.*
import com.myapplication.common.ui.theme.TacticalTheme

class AIAssistantScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var messageText by remember { mutableStateOf("") }
        val chatHistory = remember { 
            mutableStateListOf(
                AIMessage("Hi! How can I help you today?", false)
            )
        }

        Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background)) {
            // Atmosphere
            Box(
                modifier = Modifier
                    .size(500.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 100.dp, y = (-100).dp)
                    .alpha(0.06f)
                    .blur(100.dp)
                    .background(Brush.radialGradient(listOf(TacticalTheme.colors.primary, Color.Transparent)), CircleShape)
            )

            Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                // Header
                Surface(
                    modifier = Modifier.fillMaxWidth().height(90.dp),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { navigator.pop() }, modifier = Modifier.background(TacticalTheme.colors.surface.copy(alpha = 0.5f), CircleShape)) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TacticalTheme.colors.onBackground)
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text("HELP CENTER", style = TacticalTheme.typography.subtitle1, color = TacticalTheme.colors.primary)
                                Text("MY ASSISTANT", style = TacticalTheme.typography.h6, color = TacticalTheme.colors.onBackground)
                            }
                        }
                        
                        IconButton(onClick = { /* History */ }, modifier = Modifier.background(TacticalTheme.colors.surface.copy(alpha = 0.5f), CircleShape)) {
                            Icon(Icons.Default.History, null, tint = TacticalTheme.colors.primary, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // Chat Area
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(vertical = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(chatHistory) { msg ->
                        ChatBubble(msg)
                    }
                }

                // AI Neural Input
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp)
                        .navigationBarsPadding(),
                    shape = TacticalTheme.shapes.large,
                    color = TacticalTheme.colors.surface.copy(alpha = 0.9f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TacticalTheme.colors.onSurface.copy(alpha = 0.08f)),
                    elevation = 24.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TacticalTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            label = "Type a message...",
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(Modifier.width(12.dp))

                        IconButton(
                            onClick = {
                                if (messageText.isNotEmpty()) {
                                    chatHistory.add(AIMessage(messageText, true))
                                    messageText = ""
                                    chatHistory.add(AIMessage("Checking... I've updated your options.", false))
                                }
                            },
                            modifier = Modifier.size(52.dp).background(TacticalTheme.colors.primary, CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, null, tint = TacticalTheme.colors.onPrimary, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }
        }
    }
}

data class AIMessage(val text: String, val isUser: Boolean)

@Composable
fun ChatBubble(msg: AIMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 24.dp, 
                topEnd = 24.dp, 
                bottomStart = if (msg.isUser) 24.dp else 4.dp, 
                bottomEnd = if (msg.isUser) 4.dp else 24.dp
            ),
            color = if (msg.isUser) TacticalTheme.colors.primary.copy(alpha = 0.12f) else TacticalTheme.colors.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (msg.isUser) TacticalTheme.colors.primary.copy(alpha = 0.3f) else TacticalTheme.colors.onSurface.copy(alpha = 0.05f))
        ) {
            Text(
                text = msg.text, 
                color = TacticalTheme.colors.onSurface, 
                modifier = Modifier.padding(18.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp
            )
        }
        Text(
            text = (if (msg.isUser) "YOU" else "ASSISTANT").uppercase(), 
            fontSize = 9.sp, 
            color = TacticalTheme.extraColors.MutedSlate, 
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            letterSpacing = 1.sp
        )
    }
}
