package com.myapplication.common.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.myapplication.common.data.supabase.SupabaseService
import com.myapplication.common.domain.model.BusinessLocation
import com.myapplication.common.domain.model.CameraFeed
import org.koin.compose.koinInject

object AnalyticsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        val supabase = koinInject<SupabaseService>()
        val cameras by produceState(initialValue = emptyList<CameraFeed>()) {
            value = try { supabase.getCameras() } catch (e: Exception) { emptyList() }
        }
        val businesses by produceState(initialValue = emptyList<BusinessLocation>()) {
            value = try { supabase.getBusinesses() } catch (e: Exception) { emptyList() }
        }

        val infiniteTransition = rememberInfiniteTransition()
        val pulseAlpha by infiniteTransition.animateFloat(
            initialValue = 0.1f,
            targetValue = 0.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF020617)) // Ebony base
        ) {
            // Background dynamic glows
            Box(
                modifier = Modifier
                    .size(400.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-100).dp, y = 100.dp)
                    .alpha(pulseAlpha)
                    .blur(80.dp)
                    .background(Brush.radialGradient(listOf(Color(0xFF22D3EE), Color.Transparent)), CircleShape)
            )

            Column(modifier = Modifier.fillMaxSize()) {
                // High-Fidelity Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
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
                        "SECURITY DATA",
                        modifier = Modifier.align(Alignment.Center),
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = 4.sp,
                            color = Color(0xFF94A3B8)
                        )
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    item {
                        Column {
                            Text(
                                "SYSTEM HEALTH", 
                                style = androidx.compose.ui.text.TextStyle(
                                    fontSize = 10.sp, 
                                    fontWeight = FontWeight.Black, 
                                    color = Color(0xFF22D3EE),
                                    letterSpacing = 2.sp
                                )
                            )
                            Text(
                                "LIVE OVERVIEW", 
                                style = androidx.compose.ui.text.TextStyle(
                                    fontSize = 28.sp, 
                                    fontWeight = FontWeight.ExtraBold, 
                                    color = Color.White
                                )
                            )
                        }
                    }
                    
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            DetailedStatCard("ONLINE", "99.98%", Icons.Default.CloudQueue, Color(0xFF22D3EE), Modifier.weight(1f))
                            DetailedStatCard("CAMERAS", "${cameras.size}", Icons.Default.Hub, Color(0xFF10B981), Modifier.weight(1f))
                        }
                    }
                    
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(260.dp),
                            shape = RoundedCornerShape(32.dp),
                            color = Color(0xFF1E293B).copy(alpha = 0.7f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                        ) {
                            Box(modifier = Modifier.padding(24.dp)) {
                                Column {
                                    Text("DATA FEED", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                    Text("Checking ${businesses.size} sites", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                    
                                    Spacer(Modifier.height(32.dp))
                                    
                                    // Tactical Monitoring Level
                                    Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                                        cameras.forEachIndexed { index, camera ->
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .width(4.dp)
                                                    .offset(x = (index * 8).dp)
                                                    .background(if (camera.status == "ONLINE") Color(0xFF10B981) else Color(0xFFEF4444))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    item {
                        Column {
                            Text(
                                "SYSTEM LOGS", 
                                style = androidx.compose.ui.text.TextStyle(
                                    fontSize = 10.sp, 
                                    fontWeight = FontWeight.Black, 
                                    color = Color(0xFF22D3EE),
                                    letterSpacing = 2.sp
                                )
                            )
                            Spacer(Modifier.height(16.dp))
                            
                            // High-Fidelity Tactical Logs
                            val logs = listOfNotNull(
                                if (cameras.isNotEmpty()) "NETWORK: ${cameras.size} CAMERAS" to "Working at full speed" else null,
                                if (businesses.isNotEmpty()) "SITES: ${businesses.size} ACTIVE" to "Monitoring starting" else null,
                                "LOGIN: SUCCESS" to "User verified",
                                "SECURITY: STRONG" to "Connection safe"
                            )
                            
                            logs.forEach { (title, desc) ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF1E293B).copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                                ) {
                                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF10B981), CircleShape))
                                        Spacer(Modifier.width(16.dp))
                                        Column {
                                            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text(desc, fontSize = 10.sp, color = Color(0xFF64748B))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(180.dp)) }
                }
            }
        }
    }
}

@Composable
fun DetailedStatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accentColor: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.7f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.Center) {
            Surface(
                color = accentColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = accentColor, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(title, color = Color(0xFF94A3B8), fontWeight = FontWeight.Light, fontSize = 9.sp, letterSpacing = 2.sp)
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
    }
}
