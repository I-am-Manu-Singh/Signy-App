package com.myapplication.common.ui.tabs

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.myapplication.common.domain.model.ChatMessage
import com.myapplication.common.ui.theme.TacticalTheme

object AssistantTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.SmartToy)
            return remember { TabOptions(index = 10u, title = "Assists", icon = icon) }
        }

    @Composable
    override fun Content() {
        var messageInput by remember { mutableStateOf("") }
        val chatHistory = remember {
            mutableStateListOf(ChatMessage("SYSTEM ONLINE: How can I help you today?", false))
        }

        val infiniteTransition = rememberInfiniteTransition()
        val pulseAlpha by
                infiniteTransition.animateFloat(
                        initialValue = 0.05f,
                        targetValue = 0.15f,
                        animationSpec =
                                infiniteRepeatable(
                                        animation = tween(5000, easing = LinearEasing),
                                        repeatMode = RepeatMode.Reverse
                                )
                )

        Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background).imePadding()) {
            // Background tactical pulse
            Box(
                    modifier =
                            Modifier.size(600.dp)
                                    .align(Alignment.BottomEnd)
                                    .offset(x = 200.dp, y = 200.dp)
                                    .alpha(pulseAlpha)
                                    .blur(100.dp)
                                    .background(
                                            Brush.radialGradient(
                                                    listOf(TacticalTheme.colors.primary, Color.Transparent)
                                            ),
                                            CircleShape
                                    )
            )

            Column(modifier = Modifier.fillMaxSize()) {

                Surface(modifier = Modifier.weight(1f).fillMaxWidth(), color = Color.Transparent) {
                    LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            reverseLayout = true
                    ) { items(chatHistory.reversed()) { msg -> NeuralChatBubble(msg) } }
                }

                // Neural Input Section
                Surface(
                        modifier =
                                Modifier.padding(24.dp)
                                        .padding(bottom = 80.dp)
                                        .fillMaxWidth()
                                        .height(64.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = TacticalTheme.colors.surface.copy(alpha = 0.8f),
                        border = BorderStroke(1.dp, TacticalTheme.colors.onSurface.copy(alpha = 0.1f))
                ) {
                    Row(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                                value = messageInput,
                                onValueChange = { messageInput = it },
                                modifier = Modifier.weight(1f),
                                textStyle =
                                        androidx.compose.ui.text.TextStyle(
                                                color = TacticalTheme.colors.onSurface,
                                                fontSize = 14.sp
                                        ),
                                cursorBrush = SolidColor(TacticalTheme.colors.primary),
                                decorationBox = { innerTextField ->
                                    if (messageInput.isEmpty()) {
                                        Text(
                                                "Type a question...",
                                                color = TacticalTheme.extraColors.MutedSlate,
                                                fontSize = 14.sp
                                        )
                                    }
                                    innerTextField()
                                }
                        )
                        IconButton(
                                onClick = {
                                    if (messageInput.isNotEmpty()) {
                                        chatHistory.add(ChatMessage(messageInput, true))
                                        chatHistory.add(
                                                ChatMessage("Searching for answers...", false)
                                        )
                                        messageInput = ""
                                    }
                                }
                        ) {
                            Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    null,
                                    tint = TacticalTheme.colors.primary,
                                    modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NeuralChatBubble(message: ChatMessage) {
    Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start) {
            Surface(
                    color =
                            if (message.isUser) TacticalTheme.colors.primary.copy(alpha = 0.15f)
                            else TacticalTheme.colors.surface.copy(alpha = 0.8f),
                    shape =
                            RoundedCornerShape(
                                    topStart = 24.dp,
                                    topEnd = 24.dp,
                                    bottomStart = if (message.isUser) 24.dp else 4.dp,
                                    bottomEnd = if (message.isUser) 4.dp else 24.dp
                            ),
                    border =
                            BorderStroke(
                                    1.dp,
                                    if (message.isUser) TacticalTheme.colors.primary.copy(alpha = 0.3f)
                                    else TacticalTheme.colors.onSurface.copy(alpha = 0.05f)
                            )
            ) {
                Text(
                        text = message.text,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                        color = TacticalTheme.colors.onSurface,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                )
            }
            Text(
                    if (message.isUser) "YOU" else "ASSISTANT",
                    color = TacticalTheme.extraColors.MutedSlate,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp)
            )
        }
    }
}
