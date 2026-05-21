package com.myapplication.common.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import com.myapplication.common.ui.components.RTSPPlayer

class CameraDetailScreen(val cameraName: String, val rtspUrl: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var isRecording by remember { mutableStateOf(false) }
        var aiTuning by remember { mutableStateOf(0.7f) }

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020617))) {
            Column(modifier = Modifier.fillMaxSize()) {
                // High-Density Video Terminal
                Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    com.myapplication.common.ui.components.RTSPPlayer(url = rtspUrl, modifier = Modifier.fillMaxSize())
                    
                    // Vision Overlay Header (Ambiguity Safe)
                    Surface(
                        modifier = Modifier.windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.Companion.statusBars).fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                        color = Color.Transparent
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { navigator.pop() }, modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)) {
                                 Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                            }
                            Spacer(Modifier.width(16.dp))
                            Surface(color = Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp)) {
                                Text(cameraName.uppercase(), modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }

                    // Vital Overlay
                    Box(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                             Surface(color = Color.Red, shape = CircleShape, modifier = Modifier.size(8.dp)) {}
                             Spacer(Modifier.width(8.dp))
                             Text("LIVE - 1080P - 60 FPS", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    // PTZ Tactile Controller
                    Text("PTZ COMMAND MATRIX", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Spacer(Modifier.height(20.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                         JoystickControl()
                    }

                    Spacer(Modifier.height(40.dp))

                    // Playback Intelligence Timeline
                    Text("PLAYBACK TEMPORAL GRID", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Spacer(Modifier.height(16.dp))
                    Surface(modifier = Modifier.fillMaxWidth().height(60.dp), color = Color(0xFF1E293B), shape = RoundedCornerShape(16.dp)) {
                        // Digital Scrubber Visualizer
                        Box(contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.fillMaxSize()) { /* Drawing temporal ticks */ }
                            Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(Color(0xFF22D3EE)))
                        }
                    }

                    Spacer(Modifier.height(40.dp))

                    // AI Detection Precision Tuning
                    Text("AI DETECTION PRECISION: ${(aiTuning * 100).toInt()}%", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Slider(value = aiTuning, onValueChange = { aiTuning = it }, colors = SliderDefaults.colors(thumbColor = Color(0xFF22D3EE), activeTrackColor = Color(0xFF22D3EE)))

                    Spacer(Modifier.weight(1f))

                    // Tactical Core Actions
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        PillAction(if (isRecording) "STOP ARCHIVING" else "START ARCHIVING", Icons.Default.FiberManualRecord, if (isRecording) Color.Red else Color(0xFF22D3EE), Modifier.weight(1f)) { isRecording = !isRecording }
                        PillAction("SAVE CLOUD", Icons.Default.CloudUpload, Color(0xFF818CF8), Modifier.weight(1f)) { }
                    }
                }
            }
        }
    }
}

@Composable
fun JoystickControl() {
    Surface(modifier = Modifier.size(140.dp), shape = CircleShape, color = Color(0xFF1E293B).copy(alpha = 0.5f), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))) {
        Box(contentAlignment = Alignment.Center) {
            Icon(Icons.Default.KeyboardArrowUp, null, modifier = Modifier.align(Alignment.TopCenter).padding(8.dp).size(24.dp), tint = Color(0xFF64748B))
            Icon(Icons.Default.KeyboardArrowDown, null, modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp).size(24.dp), tint = Color(0xFF64748B))
            Icon(Icons.Default.KeyboardArrowLeft, null, modifier = Modifier.align(Alignment.CenterStart).padding(8.dp).size(24.dp), tint = Color(0xFF64748B))
            Icon(Icons.Default.KeyboardArrowRight, null, modifier = Modifier.align(Alignment.CenterEnd).padding(8.dp).size(24.dp), tint = Color(0xFF64748B))
            
            Surface(modifier = Modifier.size(50.dp), shape = CircleShape, color = Color(0xFF22D3EE)) {}
        }
    }
}

@Composable
fun PillAction(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(modifier = modifier.height(56.dp).clickable { onClick() }, shape = RoundedCornerShape(16.dp), color = color.copy(alpha = 0.1f), border = BorderStroke(1.dp, color.copy(alpha = 0.3f))) {
        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Black)
        }
    }
}
