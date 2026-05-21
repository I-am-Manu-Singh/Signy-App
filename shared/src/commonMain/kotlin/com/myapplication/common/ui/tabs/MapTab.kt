package com.myapplication.common.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.myapplication.common.ui.components.GoogleMapView

object MapTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Filled.LocationOn)
            return remember {
                TabOptions(
                    index = 2u,
                    title = "Locate",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020617))) {
            Column(modifier = Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).fillMaxWidth().height(100.dp),
                    color = Color.Transparent
                ) {
                    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp), verticalArrangement = Arrangement.Center) {
                        Text(
                            "SYSTEM MAP", 
                            style = androidx.compose.ui.text.TextStyle(
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Light,
                                letterSpacing = 4.sp,
                                color = Color(0xFF22D3EE)
                            )
                        )
                        Text(
                            "NODE PLACEMENT", 
                            style = androidx.compose.ui.text.TextStyle(
                                fontSize = 32.sp, 
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f).padding(24.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(32.dp),
                        color = Color(0xFF1E293B).copy(alpha = 0.8f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        GoogleMapView(modifier = Modifier.fillMaxSize())
                    }
                    
                    Surface(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFF1E293B).copy(alpha = 0.95f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                        elevation = 8.dp
                    ) {
                        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Business, null, tint = Color(0xFF22D3EE), modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text("MAIN HUB", fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
                                Text("2 ACTIVE NODES NEARBY", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Light, letterSpacing = 1.sp)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
