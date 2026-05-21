package com.myapplication.common.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.myapplication.common.data.supabase.SupabaseService
import com.myapplication.common.domain.model.BusinessLocation
import com.myapplication.common.domain.model.CameraFeed
import com.myapplication.common.ui.components.CameraMatrixItem
import com.myapplication.common.ui.tabs.EmptyCameraGrid
import org.koin.compose.koinInject

class BusinessDetailScreen(val business: BusinessLocation) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val supabase = koinInject<SupabaseService>()
        
        val camerasState = produceState(initialValue = emptyList<CameraFeed>()) {
            value = supabase.getCamerasForBusiness(business.id)
        }
        val cameras = camerasState.value

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020617))) {
            Column(modifier = Modifier.fillMaxSize()) {
                // TACTICAL NODE HEADER (Google Business Style)
                Surface(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).fillMaxWidth().height(140.dp),
                    color = Color.Transparent
                ) {
                    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { navigator.pop() }, modifier = Modifier.size(32.dp).background(Color(0xFF1E293B).copy(alpha = 0.5f), CircleShape)) {
                                 Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.width(16.dp))
                            Text("NODE DETAILS", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                             Column {
                                 Text(business.name.uppercase(), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                                 Row(verticalAlignment = Alignment.CenterVertically) {
                                      Icon(Icons.Default.Star, null, tint = Color(0xFFFACC15), modifier = Modifier.size(12.dp))
                                      Spacer(Modifier.width(4.dp))
                                      Text("4.9 · 1.2K PROTOCOL LOGS", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                 }
                             }
                             Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.1f),
                                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                             ) {
                                 Text("OPERATIONAL", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Black)
                             }
                        }
                    }
                }

                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 40.dp)) {
                    // ACTION CORE (Quick Access Features)
                    item {
                        Row(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                             ActionButton(Modifier.weight(1f), "SECURE CHAT", Icons.Default.ChatBubble, Color(0xFF22D3EE)) {
                                 navigator.push(ChatSessionScreen(business.id, business.name))
                             }
                             ActionButton(Modifier.weight(1f), "NAVIGATE", Icons.Default.Directions, Color(0xFF818CF8)) { }
                             ActionButton(Modifier.weight(1f), "BROADCAST", Icons.Default.RecordVoiceOver, Color(0xFFF472B6)) { }
                        }
                    }

                    // NODE METADATA (Google Business Parity)
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = Color(0xFF1E293B).copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                 MetadataItem(Icons.Default.Category, "PROTOCOL", business.category ?: "SITUATIONAL HUB")
                                 Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))
                                 MetadataItem(Icons.Default.LocationOn, "COORDINATES", "${business.latitude}, ${business.longitude}")
                                 Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))
                                 MetadataItem(Icons.Default.AccessTime, "OPERATIONAL HOURS", "24/7 SURVEILLANCE ACTIVE")
                            }
                        }
                    }

                    // SITUATIONAL GRID (Ezviz Style)
                    item {
                        Row(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("SENSORY GRID", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                            Text("${cameras.size} ACTIVE NODES", color = Color(0xFF22D3EE), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    if (cameras.isEmpty()) {
                        item { Box(modifier = Modifier.padding(horizontal = 24.dp)) { EmptyCameraGrid { navigator.push(RegisterCameraScreen()) } } }
                    } else {
                        items(cameras) { camera ->
                            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                                CameraMatrixItem(camera, isCompact = false) {
                                    navigator.push(CameraDetailScreen(camera.name, camera.url))
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(modifier: Modifier, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(84.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1E293B),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
             Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
             Spacer(Modifier.height(8.dp))
             Text(label, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun MetadataItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(modifier = Modifier.size(32.dp), shape = RoundedCornerShape(8.dp), color = Color(0xFF22D3EE).copy(alpha = 0.1f)) {
             Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = Color(0xFF22D3EE), modifier = Modifier.size(16.dp)) }
        }
        Spacer(Modifier.width(16.dp))
        Column {
             Text(label, color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Black)
             Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}
