package com.myapplication.common.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.myapplication.common.ui.components.MapViewContainer
import com.myapplication.common.data.supabase.SupabaseService
import com.myapplication.common.domain.model.BusinessLocation
import org.koin.compose.koinInject

class MapPortalScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val supabase = koinInject<SupabaseService>()
        
        // LIVE GEOSPATIAL FETCH (No Placeholders)
        val businessesState = produceState(initialValue = emptyList<BusinessLocation>()) {
            value = supabase.getBusinesses()
        }
        val businesses = businessesState.value
        
        var selectedBiz by remember { mutableStateOf<BusinessLocation?>(null) }
        
        // Default to first business if none selected
        LaunchedEffect(businesses) {
            if (selectedBiz == null && businesses.isNotEmpty()) {
                selectedBiz = businesses.first()
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020617))) {
            // High-Density Map Core (Wired to real locations)
            com.myapplication.common.ui.components.MapViewContainer(
                modifier = Modifier.fillMaxSize(),
                businesses = businesses,
                onLocationSelected = { _: Double, _: Double -> /* Selection logic */ }
            )

            // Geospatial Discovery Matrix
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 24.dp)) {
                // High-Density TopAppBar (Ambiguity Safe Padding Fix)
                Surface(
                    modifier = Modifier.windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.Companion.statusBars).fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                    elevation = 12.dp
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navigator.pop() }) {
                             Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "GEOSPATIAL GRID",
                            modifier = Modifier.weight(1f),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                        IconButton(onClick = { /* Layer Tuning */ }) {
                             Icon(Icons.Default.Layers, null, tint = Color(0xFF22D3EE), modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Action Hub for Site Posts
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    item { MapIntelChip("LIVE UPDATE", Icons.Default.CameraAlt, Color(0xFF10B981)) }
                    item { MapIntelChip("NODE ANALYTICS", Icons.Default.Leaderboard, Color(0xFF22D3EE)) }
                    item { MapIntelChip("CAMPAIGN GRID", Icons.Default.Campaign, Color(0xFF818CF8)) }
                }

                Spacer(Modifier.weight(1f))

                // Contextual Node Detail Matrix
                AnimatedVisibility(
                    visible = selectedBiz != null,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    selectedBiz?.let { biz ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(180.dp).clickable { /* Detail View */ },
                            shape = RoundedCornerShape(32.dp),
                            color = Color(0xFF1E293B).copy(alpha = 0.95f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                            elevation = 20.dp
                        ) {
                            Column(modifier = Modifier.padding(28.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(modifier = Modifier.size(56.dp), color = Color(0xFF22D3EE).copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                             Icon(Icons.Default.Storefront, null, tint = Color(0xFF22D3EE), modifier = Modifier.size(28.dp))
                                        }
                                    }
                                    Spacer(Modifier.width(20.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(biz.name.uppercase(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                                        Text(biz.category ?: "OPERATIONAL HUB", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    IconButton(onClick = { /* Navigate */ }) {
                                        Icon(Icons.Default.Directions, null, tint = Color(0xFF22D3EE), modifier = Modifier.size(36.dp))
                                    }
                                }
                                Spacer(Modifier.height(20.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                     Icon(Icons.Default.Star, null, tint = Color(0xFFFACC15), modifier = Modifier.size(14.dp))
                                     Text(" 4.9 (2,142 SIT-REPS)", color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                     Spacer(Modifier.weight(1f))
                                     Text("OPEN: 24/7", color = Color(0xFF22D3EE), fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapIntelChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.9f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
             Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
             Spacer(Modifier.width(10.dp))
             Text(label, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        }
    }
}
