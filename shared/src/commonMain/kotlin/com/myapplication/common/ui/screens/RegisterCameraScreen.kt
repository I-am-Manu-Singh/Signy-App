package com.myapplication.common.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.myapplication.common.data.supabase.SupabaseService
import com.myapplication.common.domain.model.CameraFeed
import com.myapplication.common.domain.model.BusinessLocation
import kotlinx.coroutines.launch
import com.myapplication.common.ui.components.TacticalTextField
import com.myapplication.common.ui.components.TacticalButton
import com.myapplication.common.ui.theme.TacticalTheme
import org.koin.compose.koinInject

class RegisterCameraScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val supabase = koinInject<SupabaseService>()
        val scope = rememberCoroutineScope()
        
        var name by remember { mutableStateOf("") }
        var url by remember { mutableStateOf("") }
        var selectedBusiness by remember { mutableStateOf<BusinessLocation?>(null) }
        var showBusinessPicker by remember { mutableStateOf(false) }

        val businessesState = produceState(initialValue = emptyList<BusinessLocation>()) {
            value = supabase.getBusinesses()
        }
        val businesses = businessesState.value

        Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Tactical Header
                Surface(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).fillMaxWidth().height(64.dp),
                    color = Color.Transparent
                ) {
                    Row(modifier = Modifier.padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navigator.pop() }) {
                             Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TacticalTheme.colors.onBackground)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("ADD NODE", color = TacticalTheme.colors.onBackground, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }
                }

                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Text("NODE IDENTITY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TacticalTheme.extraColors.MutedSlate, letterSpacing = 2.sp)
                    Spacer(Modifier.height(16.dp))
                    
                    TacticalTextField(
                        value = name, 
                        onValueChange = { name = it }, 
                        label = "CAMERA NAME", 
                        leadingIcon = Icons.Default.Videocam
                    )
                    Spacer(Modifier.height(16.dp))
                    TacticalTextField(
                        value = url, 
                        onValueChange = { url = it }, 
                        label = "RTSP STREAM URL", 
                        leadingIcon = Icons.Default.Link
                    )
                    
                    Spacer(Modifier.height(32.dp))
                    
                    Text("BUSINESS LINKAGE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TacticalTheme.extraColors.MutedSlate, letterSpacing = 2.sp)
                    Spacer(Modifier.height(16.dp))
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { showBusinessPicker = true },
                        shape = TacticalTheme.shapes.medium,
                        color = TacticalTheme.colors.surface.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, TacticalTheme.colors.onSurface.copy(alpha = 0.05f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                             Icon(Icons.Default.Storefront, null, tint = TacticalTheme.colors.primary, modifier = Modifier.size(20.dp))
                             Spacer(Modifier.width(16.dp))
                             Text(
                                 selectedBusiness?.name?.uppercase() ?: "SELECT TARGET BUSINESS HUB", 
                                 color = if (selectedBusiness == null) TacticalTheme.extraColors.MutedSlate else TacticalTheme.colors.onSurface, 
                                 fontSize = 12.sp, 
                                 fontWeight = FontWeight.Bold
                             )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    TacticalButton(
                        text = "REGISTER NODE",
                        onClick = {
                            if (name.isNotEmpty() && url.isNotEmpty()) {
                                scope.launch {
                                    try {
                                        supabase.addCamera(
                                            CameraFeed(
                                                id = com.benasher44.uuid.uuid4().toString(),
                                                name = name,
                                                url = url,
                                                status = "ONLINE",
                                                businessId = selectedBusiness?.id
                                            )
                                        )
                                        navigator.pop()
                                    } catch (e: Exception) {
                                        println("CLUSTER SYNC ERROR: ${e.message}")
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                }
            }

            // Business Selection Dropdown
            if (showBusinessPicker) {
                Surface(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f)),
                    color = Color.Transparent
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(0.9f).height(400.dp),
                            shape = TacticalTheme.shapes.large,
                            color = TacticalTheme.colors.surface,
                            border = BorderStroke(1.dp, TacticalTheme.colors.onSurface.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text("SELECT TARGET CLUSTER", color = TacticalTheme.colors.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                                Spacer(Modifier.height(16.dp))
                                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(businesses) { biz ->
                                         Surface(
                                             modifier = Modifier.fillMaxWidth().clickable { 
                                                 selectedBusiness = biz
                                                 showBusinessPicker = false
                                             },
                                             shape = TacticalTheme.shapes.small,
                                             color = TacticalTheme.colors.background.copy(alpha = 0.5f)
                                         ) {
                                              Text(biz.name.uppercase(), modifier = Modifier.padding(16.dp), color = TacticalTheme.colors.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                         }
                                    }
                                }
                                TextButton(onClick = { showBusinessPicker = false }, modifier = Modifier.align(Alignment.End)) {
                                     Text("CANCEL", color = TacticalTheme.extraColors.MutedSlate, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
