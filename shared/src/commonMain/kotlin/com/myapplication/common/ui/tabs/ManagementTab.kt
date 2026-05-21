package com.myapplication.common.ui.tabs

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.myapplication.common.ui.screens.*
import com.myapplication.common.domain.model.BusinessLocation
import com.myapplication.common.data.supabase.SupabaseService
import com.myapplication.common.ui.theme.TacticalTheme
import com.myapplication.common.ui.components.*
import org.koin.compose.koinInject

object ManagementTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Filled.Hub)
            return remember {
                TabOptions(
                    index = 2u,
                    title = "Devices",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
        val supabase = koinInject<SupabaseService>()
        
        val businessesState = produceState(initialValue = emptyList<BusinessLocation>()) {
            value = supabase.getBusinesses()
        }
        val businesses = businessesState.value
        
        Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background)) {
            Column(modifier = Modifier.fillMaxSize()) {

                TacticalCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(52.dp), shape = CircleShape, color = TacticalTheme.colors.primary.copy(alpha = 0.1f)) {
                            Box(contentAlignment = Alignment.Center) {
                                 Icon(Icons.Default.Podcasts, null, tint = TacticalTheme.colors.primary, modifier = Modifier.size(26.dp))
                             }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("NETWORK STATUS", color = TacticalTheme.extraColors.MutedSlate, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("${businesses.size} DEVICES ACTIVE", color = TacticalTheme.colors.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    "QUICK ROUTINES", 
                    modifier = Modifier.padding(start = 24.dp, top = 24.dp),
                    color = TacticalTheme.extraColors.MutedSlate, 
                    fontSize = 10.sp, 
                    fontWeight = FontWeight.Bold, 
                    letterSpacing = 2.sp
                )
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { RoutineActionCard("HOME", Icons.Default.Home, TacticalTheme.colors.primary) }
                    item { RoutineActionCard("OFFICE", Icons.Default.Work, Color(0xFFFACC15)) }
                    item { RoutineActionCard("AWAY", Icons.Default.DirectionsRun, TacticalTheme.colors.error) }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("MY REGISTRY", color = TacticalTheme.extraColors.MutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                            TextButton(onClick = { rootNavigator.push(RegisterBusinessScreen()) }) {
                                Text("REGISTER NEW", color = TacticalTheme.colors.primary, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                    
                    if (businesses.isEmpty()) {
                        item { EmptyRegistryCard { rootNavigator.push(RegisterBusinessScreen()) } }
                    } else {
                        items(businesses) { biz -> RealDeviceCard(biz) { /* Details */ } }
                    }
                }
            }
        }
    }
}

@Composable
fun RoutineActionCard(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    TacticalCard(
        modifier = Modifier.width(130.dp).height(100.dp).clickable { }
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.size(36.dp).background(color.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            }
            Text(label, color = TacticalTheme.colors.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RealDeviceCard(biz: BusinessLocation, onClick: () -> Unit) {
    TacticalCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = TacticalTheme.colors.primary.copy(alpha = 0.1f), shape = CircleShape, modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) {
                     Icon(Icons.Default.Dns, null, tint = TacticalTheme.colors.primary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(biz.name, color = TacticalTheme.colors.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(biz.category ?: "Standard Node", color = TacticalTheme.extraColors.MutedSlate, fontSize = 11.sp)
            }
            Surface(color = Color(0xFF10B981).copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                Text("ONLINE", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color(0xFF10B981), fontSize = 8.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun EmptyRegistryCard(onAdd: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)) {
        Icon(Icons.Default.DeveloperBoard, null, modifier = Modifier.size(56.dp), tint = TacticalTheme.colors.surface)
        Spacer(Modifier.height(16.dp))
        Text("No devices found", color = TacticalTheme.colors.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Register your hardware to manage it here", color = TacticalTheme.extraColors.MutedSlate, fontSize = 12.sp)
        Spacer(Modifier.height(24.dp))
        TacticalButton(text = "ADD DEVICE", onClick = onAdd, modifier = Modifier.width(180.dp).height(48.dp))
    }
}

