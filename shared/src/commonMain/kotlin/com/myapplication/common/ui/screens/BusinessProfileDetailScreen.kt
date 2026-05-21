package com.myapplication.common.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.myapplication.common.domain.model.BusinessLocation
import com.myapplication.common.ui.components.MapViewContainer

class BusinessProfileDetailScreen(val business: BusinessLocation) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scrollState = rememberScrollState()

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020617))) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                // Geospatial Header (G-Maps style)
                Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                    MapViewContainer(
                        modifier = Modifier.fillMaxSize(),
                        businesses = listOf(business)
                    )
                    
                    // Header Overlay
                    Row(
                        modifier = Modifier.statusBarsPadding().fillMaxWidth().padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { navigator.pop() }, modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                        }
                        IconButton(onClick = { /* Share */ }, modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)) {
                            Icon(Icons.Default.Share, null, tint = Color.White)
                        }
                    }
                }

                // Node Identity Dashboard (G-Business style)
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(business.name.uppercase(), fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 1.sp)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.Verified, null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                    }
                    Text(business.category ?: "Node Profile", color = Color(0xFF22D3EE), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    
                    Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("4.9", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        repeat(5) { Icon(Icons.Default.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(10.dp)) }
                        Text(" (124 TACTICAL REVIEWS)", color = Color(0xFF475569), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(32.dp))

                    // Tactical Command Actions
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        BizActionBtn(Icons.Default.Directions, "ROUTE", Color(0xFF22D3EE))
                        BizActionBtn(Icons.Default.Call, "INTEL", Color.White)
                        BizActionBtn(Icons.AutoMirrored.Filled.Chat, "MESSAGE", Color.White)
                        BizActionBtn(Icons.Default.Language, "UPLINK", Color.White)
                    }

                    Spacer(Modifier.height(40.dp))

                    // Site Vitality Analytics (G-Business style)
                    Text("NODE PERFORMANCE ANALYTICS", fontSize = 10.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFF1E293B).copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("SIGNAL STRENGTH", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                    Text("-64 dBm", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Black)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("UPTIME", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                    Text("99.98%", fontSize = 18.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Black)
                                }
                            }
                            Spacer(Modifier.height(20.dp))
                            // Signal Graph
                            Row(modifier = Modifier.fillMaxWidth().height(60.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                repeat(12) { i ->
                                    val height = (20..50).random().dp
                                    Box(modifier = Modifier.weight(1f).height(height).background(Color(0xFF22D3EE).copy(alpha = 0.3f), RoundedCornerShape(4.dp)))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(40.dp))

                    // Operational Parameters (Info Rows)
                    Text("SITE LOGISTICS", fontSize = 10.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(16.dp))
                    BizInfoRow(Icons.Default.LocationOn, business.address ?: "GEOSPATIAL COORDINATES NOT SYNCED")
                    BizInfoRow(Icons.Default.AccessTime, "Secure Access Required 24/7 Operations")
                    BizInfoRow(Icons.Default.VerifiedUser, "Signy Encryption Standard AES-256")
                    BizInfoRow(Icons.Default.Devices, "4 Active Camera Nodes Linked")

                    Spacer(Modifier.height(40.dp))

                    // Intelligence Reviews Hub
                    Text("TACTICAL REPORTS & REVIEWS", fontSize = 10.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(20.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        repeat(3) { i ->
                            ReviewCard("OPERATOR ${'A' + i}", "High performance node. Signal stability is unmatched in this sector.")
                        }
                    }
                    
                    Spacer(Modifier.height(120.dp))
                }
            }
        }
    }
}

@Composable
fun BizActionBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = Color(0xFF1E293B),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(24.dp)) }
        }
        Text(label, modifier = Modifier.padding(top = 8.dp), fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BizInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(modifier = Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color(0xFF475569), modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(16.dp))
        Text(text, color = Color.White, fontSize = 13.sp)
    }
}

@Composable
fun ReviewCard(name: String, text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.3f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = Color(0xFF1E293B)) {
                    Box(contentAlignment = Alignment.Center) { Text(name.take(1), color = Color(0xFF22D3EE), fontWeight = FontWeight.Black) }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row { repeat(5) { Icon(Icons.Default.Star, null, tint = Color(0xFFFACC15), modifier = Modifier.size(10.dp)) } }
                }
                Spacer(Modifier.weight(1f))
                Text("2d ago", color = Color(0xFF475569), fontSize = 10.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text(text, color = Color(0xFF94A3B8), fontSize = 12.sp, lineHeight = 18.sp)
        }
    }
}
