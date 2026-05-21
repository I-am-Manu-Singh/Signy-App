package com.myapplication.common.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.myapplication.common.ui.components.*
import com.myapplication.common.ui.theme.TacticalTheme
import kotlinx.coroutines.delay

class ActiveCallScreen(
    val name: String, 
    val isVideo: Boolean,
    val meetingId: String? = null
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val webRTCManager = org.koin.compose.koinInject<com.myapplication.common.domain.webrtc.WebRTCManager>()
        
        var isMuted by remember { mutableStateOf(false) }
        var isCameraOff by remember { mutableStateOf(!isVideo) }
        var isSpeakerOn by remember { mutableStateOf(true) }
        var timerSeconds by remember { mutableStateOf(0) }

        val permissions = com.myapplication.common.util.rememberBasePermissionManager()
        var hasRequiredPermissions by remember { mutableStateOf(false) }
        var isCallInitialized by remember { mutableStateOf(false) }

        val localTrack by webRTCManager.localTrack.collectAsState()
        val remoteTrack by webRTCManager.remoteTrack.collectAsState()

        permissions.requestPermissions { success ->
            hasRequiredPermissions = success
        }

        LaunchedEffect(hasRequiredPermissions) {
            if (hasRequiredPermissions && !isCallInitialized) {
                try {
                    isCallInitialized = true
                    val callId = name.lowercase().filter { it.isLetterOrDigit() } + "_session"
                    webRTCManager.startCall(callId, isVideo)
                    
                    while (true) {
                        delay(1000)
                        timerSeconds++
                    }
                } catch (e: Exception) {
                    println("Call Protocol Interface Error: ${e.message}")
                }
            }
        }
        
        DisposableEffect(Unit) {
            onDispose { webRTCManager.endCall() }
        }

        val timerText = remember(timerSeconds) {
            val mins = timerSeconds / 60
            val secs = timerSeconds % 60
            "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
        }

        Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background)) {
            // Secure Environment Visuals
            Box(
                modifier = Modifier
                    .size(600.dp)
                    .align(Alignment.Center)
                    .alpha(0.05f)
                    .blur(100.dp)
                    .background(Brush.radialGradient(listOf(TacticalTheme.colors.primary, Color.Transparent)), CircleShape)
            )

            if (hasRequiredPermissions) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // SECURE STREAM GRID
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 100.dp, bottom = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                ParticipantPreviewNode(
                                    name = name, 
                                    track = remoteTrack, 
                                    isMicOn = true, 
                                    isCamOn = remoteTrack != null,
                                    modifier = Modifier.weight(1f).height(200.dp)
                                )
                                ParticipantPreviewNode(
                                    name = "ADMIN", 
                                    track = null, 
                                    isMicOn = false, 
                                    isCamOn = false,
                                    modifier = Modifier.weight(1f).height(200.dp)
                                )
                            }
                        }
                    }

                    // LOCAL MINI-FEED (SELF)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 140.dp, end = 24.dp)
                            .size(110.dp, 160.dp)
                            .clip(TacticalTheme.shapes.medium)
                            .background(TacticalTheme.colors.surface.copy(alpha = 0.8f))
                            .border(1.dp, TacticalTheme.colors.primary.copy(alpha = 0.3f), TacticalTheme.shapes.medium)
                    ) {
                        if (localTrack != null && !isCameraOff) {
                            com.myapplication.common.ui.components.VideoRenderer(
                                track = localTrack,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                TacticalAvatar(name = "YOU", size = 52.dp)
                                if (isCameraOff) Icon(Icons.Default.VideocamOff, null, tint = TacticalTheme.colors.error.copy(alpha = 0.6f), modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(14.dp))
                                if (isMuted) Icon(Icons.Default.MicOff, null, tint = TacticalTheme.colors.error.copy(alpha = 0.6f), modifier = Modifier.align(Alignment.TopStart).padding(8.dp).size(14.dp))
                            }
                        }
                        
                        Text(
                            text = "YOU", 
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp), 
                            color = TacticalTheme.colors.primary, 
                            fontSize = 8.sp, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = TacticalTheme.colors.primary, strokeWidth = 2.dp)
                        Spacer(Modifier.height(24.dp))
                        Text("CONNECTING...", color = TacticalTheme.colors.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
            }

            // OPERATIONAL HUD
            Column(modifier = Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).fillMaxWidth().height(100.dp),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = TacticalTheme.shapes.small, 
                            color = TacticalTheme.colors.background.copy(alpha = 0.8f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(6.dp), shape = CircleShape, color = TacticalTheme.colors.secondary) {}
                                Spacer(Modifier.width(10.dp))
                                Text(timerText, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                                if (meetingId != null) {
                                    Spacer(Modifier.width(16.dp))
                                    Text("ROOM: ${meetingId.uppercase()}", color = TacticalTheme.colors.primary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        if (meetingId != null) {
                            IconButton(
                                onClick = { /* Share */ },
                                modifier = Modifier.background(TacticalTheme.colors.primary.copy(alpha = 0.15f), CircleShape).border(1.dp, TacticalTheme.colors.primary.copy(alpha = 0.3f), CircleShape)
                            ) {
                                Icon(Icons.Default.Share, null, tint = TacticalTheme.colors.primary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                // MISSION CONTROL CONSOLE
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(40.dp),
                        color = TacticalTheme.colors.surface.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                        elevation = 32.dp,
                        modifier = Modifier.padding(bottom = 28.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                             ControlActionIcon(
                                icon = if (isSpeakerOn) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                                isActive = isSpeakerOn,
                                onClick = { isSpeakerOn = !isSpeakerOn }
                             )
                             ControlActionIcon(
                                icon = if (isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                isActive = !isCameraOff,
                                onClick = { isCameraOff = !isCameraOff },
                                activeColor = TacticalTheme.colors.primary,
                                inactiveColor = TacticalTheme.colors.error
                             )
                             ControlActionIcon(
                                icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                isActive = !isMuted,
                                onClick = { isMuted = !isMuted },
                                activeColor = TacticalTheme.colors.primary,
                                inactiveColor = TacticalTheme.colors.error
                             )
                        }
                    }

                    // DISCONNECT TERMINAL
                    Surface(
                        modifier = Modifier.size(76.dp).clickable { navigator.pop() },
                        shape = CircleShape,
                        color = TacticalTheme.colors.error,
                        elevation = 16.dp,
                        border = BorderStroke(2.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Box(contentAlignment = Alignment.Center) { 
                            Icon(Icons.Default.CallEnd, null, tint = Color.White, modifier = Modifier.size(36.dp)) 
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ControlActionIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
    activeColor: Color = TacticalTheme.colors.primary,
    inactiveColor: Color = Color.White
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(52.dp).background(if (isActive) activeColor.copy(alpha = 0.15f) else Color.Transparent, CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) activeColor else inactiveColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ParticipantPreviewNode(
    name: String, 
    track: com.shepeliev.webrtckmp.VideoTrack?, 
    isMicOn: Boolean, 
    isCamOn: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = TacticalTheme.shapes.medium,
        color = TacticalTheme.colors.surface,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (track != null && isCamOn) {
                com.myapplication.common.ui.components.VideoRenderer(
                    track = track,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    TacticalAvatar(name = name, size = 64.dp)
                }
            }

            // Stream Metadata
            Row(
                modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = TacticalTheme.colors.background.copy(alpha = 0.7f), 
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = name.uppercase(), 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), 
                        color = Color.White, 
                        fontSize = 9.sp, 
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
                
                Icon(
                    imageVector = if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff, 
                    contentDescription = null, 
                    tint = if (isMicOn) TacticalTheme.colors.primary else TacticalTheme.colors.error, 
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}


