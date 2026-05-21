package com.myapplication.common.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class SearchPortalScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var query by remember { mutableStateOf("") }
        val results = remember(query) {
            if (query.isEmpty()) emptyList() 
            else listOf(
                SearchResult("FRONT GATE", "SECURITY CAMERA", Icons.Default.Videocam),
                SearchResult("ALPHA NODE", "ACTIVE OPERATOR", Icons.Default.Person),
                SearchResult("ENCRYPTION SETTINGS", "SYSTEM PANEL", Icons.Default.Settings)
            ).filter { it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }
        }

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020617))) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header (Status Bar Safety)
                Row(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("GLOBAL SEARCH", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                }

                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                    Spacer(Modifier.height(32.dp))

                    // Tactical Search Bar
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1E293B).copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, Color(0xFF22D3EE).copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Search, null, tint = Color(0xFF22D3EE), modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(16.dp))
                            BasicTextField(
                                value = query,
                                onValueChange = { query = it },
                                modifier = Modifier.weight(1f),
                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 16.sp),
                                cursorBrush = SolidColor(Color(0xFF22D3EE)),
                                decorationBox = { innerTextField ->
                                    if (query.isEmpty()) {
                                        Text("Search cameras, nodes, or logs...", color = Color(0xFF475569))
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // Results
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        item {
                            Text(
                                "SEARCH RESULTS", 
                                fontSize = 10.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color(0xFF64748B), 
                                letterSpacing = 2.sp
                            )
                        }

                        if (results.isEmpty() && query.isNotEmpty()) {
                            item {
                                Text("No tactical matches found.", color = Color(0xFF475569), fontSize = 14.sp)
                            }
                        }

                        items(results) { res ->
                            Surface(
                                modifier = Modifier.fillMaxWidth().clickable { },
                                shape = RoundedCornerShape(24.dp),
                                color = Color(0xFF1E293B).copy(alpha = 0.3f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        modifier = Modifier.size(48.dp),
                                        shape = CircleShape,
                                        color = Color(0xFF1E293B)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(res.icon, null, tint = Color(0xFF22D3EE), modifier = Modifier.size(20.dp))
                                        }
                                    }
                                    Spacer(Modifier.width(16.dp))
                                    Column {
                                        Text(res.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(res.category, color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.weight(1f))
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color(0xFF475569))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class SearchResult(val title: String, val category: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
