package com.myapplication.common.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.myapplication.common.data.supabase.SupabaseService
import com.myapplication.common.domain.model.BusinessLocation
import org.koin.compose.koinInject

class BusinessManagementScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val supabase = koinInject<SupabaseService>()
        val businesses by produceState(initialValue = emptyList<BusinessLocation>()) {
            value = try { supabase.getBusinesses() } catch (e: Exception) { emptyList() }
        }

        Scaffold(
            backgroundColor = Color(0xFF020617),
            topBar = {
                TopAppBar(
                    title = { Text("SITE MANAGER", fontWeight = FontWeight.Black, letterSpacing = 4.sp, fontSize = 14.sp) },
                    navigationIcon = {
                        IconButton(onClick = { navigator?.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                        }
                    },
                    backgroundColor = Color(0xFF020617),
                    elevation = 0.dp
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                // Tactical Map Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Radar, null, tint = Color(0xFF22D3EE).copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("SYSTEMS ONLINE", color = Color(0xFF64748B), fontSize = 10.sp, letterSpacing = 2.sp)
                    }
                }
                
                Text(
                    "SAFETY UPDATES", 
                    modifier = Modifier.padding(24.dp),
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 2.sp
                    )
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(businesses) { biz ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = Color(0xFF1E293B).copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                        ) {
                            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(44.dp),
                                    shape = CircleShape,
                                    color = Color(0xFF22D3EE).copy(alpha = 0.1f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Hub, null, tint = Color(0xFF22D3EE), modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(Modifier.width(20.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(biz.name.uppercase(), fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.White, letterSpacing = 1.sp)
                                    Text(biz.address?.uppercase() ?: "NO LOCATION DATA", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Light)
                                }
                                IconButton(onClick = {}) {
                                    Icon(Icons.Default.MoreVert, null, tint = Color(0xFF475569))
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}

data class BusinessItem(val name: String, val location: String, val status: String)

@Composable
fun BusinessCard(biz: BusinessItem) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth(), elevation = 2.dp) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Filled.Business, contentDescription = null, tint = MaterialTheme.colors.secondary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(biz.name, style = MaterialTheme.typography.h6)
                Text(biz.location, style = MaterialTheme.typography.body2, color = Color.Gray)
                Text(biz.status, style = MaterialTheme.typography.caption, color = if (biz.status.contains("Active")) Color.Green else Color.Red)
            }
        }
    }
}
