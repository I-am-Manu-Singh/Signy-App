package com.myapplication.common.ui.tabs

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.myapplication.common.domain.model.CameraFeed
import com.myapplication.common.data.supabase.SupabaseService
import com.myapplication.common.ui.components.*
import com.myapplication.common.ui.theme.TacticalTheme
import org.koin.compose.koinInject

object SecurityTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Security)
            return remember {
                TabOptions(
                    index = 0u,
                    title = "Surveillance",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
        val supabase = koinInject<SupabaseService>()
        
        var selectedFilter by remember { mutableStateOf("ALL") }
        var gridCells by remember { mutableStateOf(1) }
        var showAlarms by remember { mutableStateOf(false) }
        
        val camerasState = produceState(initialValue = emptyList<CameraFeed>()) {
            value = supabase.getCameras()
        }
        val cameras = camerasState.value

        Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background)) {
            Column(modifier = Modifier.fillMaxSize()) {

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        modifier = Modifier.weight(1f), 
                        contentPadding = PaddingValues(end = 16.dp), 
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item { TacticalChip("ALL CAMERAS", selectedFilter == "ALL", Icons.Default.Videocam) { selectedFilter = "ALL" } }
                        item { TacticalChip("ALERTS", selectedFilter == "CRIT", Icons.Default.Warning) { selectedFilter = "CRIT" } }
                    }
                    
                    Surface(
                        shape = TacticalTheme.shapes.small, 
                        color = TacticalTheme.colors.surface,
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                             GridToggleButton(gridCells == 1, Icons.Default.Rectangle) { gridCells = 1 }
                             GridToggleButton(gridCells == 2, Icons.Default.GridView) { gridCells = 2 }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = showAlarms,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        color = TacticalTheme.colors.error.copy(alpha = 0.15f),
                        shape = TacticalTheme.shapes.medium,
                        border = BorderStroke(1.dp, TacticalTheme.colors.error.copy(alpha = 0.4f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PriorityHigh, null, tint = TacticalTheme.colors.error, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("MOTION DETECTED: FRONT GATE", color = TacticalTheme.colors.onSurface, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.weight(1f))
                            Text("VIEW", color = TacticalTheme.colors.error, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.clickable { })
                        }
                    }
                }

                if (cameras.isEmpty()) {
                    EmptyCameraGrid { rootNavigator.push(RegisterCameraScreen()) }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(gridCells),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 140.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(cameras) { camera ->
                            CameraMatrixItem(camera, isCompact = gridCells == 2) {
                                rootNavigator.push(CameraDetailScreen(camera.name, camera.url))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GridToggleButton(isSelected: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(36.dp).clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) TacticalTheme.colors.primary else Color.Transparent
    ) {
        Box(contentAlignment = Alignment.Center) { 
            Icon(icon, null, tint = if (isSelected) TacticalTheme.colors.onPrimary else TacticalTheme.extraColors.MutedSlate, modifier = Modifier.size(16.dp)) 
        }
    }
}

@OptIn(androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun TacticalChip(label: String, isSelected: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) TacticalTheme.colors.primary else TacticalTheme.colors.surface,
        border = BorderStroke(1.dp, if (isSelected) TacticalTheme.colors.primary else TacticalTheme.colors.onSurface.copy(alpha = 0.05f))
    ) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (isSelected) TacticalTheme.colors.onPrimary else TacticalTheme.colors.primary, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(10.dp))
            Text(label, color = if (isSelected) TacticalTheme.colors.onPrimary else TacticalTheme.colors.onSurface, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EmptyCameraGrid(onAdd: () -> Unit) {
    TacticalCard(modifier = Modifier.padding(24.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().height(200.dp)) {
            Icon(Icons.Default.VideocamOff, null, modifier = Modifier.size(56.dp), tint = TacticalTheme.colors.surface)
            Spacer(Modifier.height(16.dp))
            Text("NO CAMERAS FOUND", style = TacticalTheme.typography.h6, color = TacticalTheme.colors.onSurface)
            Spacer(Modifier.height(12.dp))
            Text("Add your first camera to start monitoring", color = TacticalTheme.extraColors.MutedSlate, fontSize = 12.sp)
            Spacer(Modifier.height(24.dp))
            TacticalButton(text = "ADD CAMERA", onClick = onAdd, modifier = Modifier.width(200.dp))
        }
    }
}


