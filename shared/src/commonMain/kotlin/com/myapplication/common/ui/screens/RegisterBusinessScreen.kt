package com.myapplication.common.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.myapplication.common.data.supabase.SupabaseService
import com.myapplication.common.domain.model.BusinessLocation
import com.myapplication.common.ui.components.MapViewContainer
import kotlinx.coroutines.launch
import com.myapplication.common.ui.components.TacticalTextField
import com.myapplication.common.ui.components.TacticalButton
import com.myapplication.common.ui.theme.TacticalTheme
import org.koin.compose.koinInject
import io.github.jan.supabase.auth.auth

class RegisterBusinessScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val supabase = koinInject<SupabaseService>()
        val scope = rememberCoroutineScope()
        
        var name by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("") }
        var latitude by remember { mutableStateOf(37.7749) }
        var longitude by remember { mutableStateOf(-122.4194) }

        Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).fillMaxWidth().padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TacticalTheme.colors.onBackground)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("ADD BUSINESS", color = TacticalTheme.colors.onBackground, fontSize = 20.sp, fontWeight = FontWeight.Black)
                }

                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                    Text("BUSINESS IDENTITY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TacticalTheme.extraColors.MutedSlate, letterSpacing = 2.sp)
                    Spacer(Modifier.height(16.dp))
                    
                    TacticalTextField(
                        value = name, 
                        onValueChange = { name = it }, 
                        label = "BUSINESS NAME", 
                        leadingIcon = Icons.Default.Business
                    )
                    Spacer(Modifier.height(16.dp))
                    TacticalTextField(
                        value = category, 
                        onValueChange = { category = it }, 
                        label = "CATEGORY", 
                        leadingIcon = Icons.Default.Category
                    )
                    
                    Spacer(Modifier.height(32.dp))
                    
                    Text("GEOSPATIAL PLACEMENT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TacticalTheme.extraColors.MutedSlate, letterSpacing = 2.sp)
                    Spacer(Modifier.height(16.dp))
                    
                    // Map Picker Interface
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(220.dp),
                        shape = TacticalTheme.shapes.large,
                        color = TacticalTheme.colors.surface.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, TacticalTheme.colors.onSurface.copy(alpha = 0.05f))
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            MapViewContainer(
                                modifier = Modifier.fillMaxSize(),
                                onLocationSelected = { lat: Double, lng: Double ->
                                    latitude = lat
                                    longitude = lng
                                }
                            )
                            // Overlay indicator
                            Surface(
                                modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
                                shape = TacticalTheme.shapes.small,
                                color = TacticalTheme.colors.background.copy(alpha = 0.8f)
                            ) {
                                Text(
                                    "TAP TO LOCK", 
                                    modifier = Modifier.padding(10.dp),
                                    color = TacticalTheme.colors.primary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        CoordinateDisplay("LATITUDE", latitude.toString(), Modifier.weight(1f))
                        CoordinateDisplay("LONGITUDE", longitude.toString(), Modifier.weight(1f))
                    }

                    Spacer(Modifier.weight(1f))

                    TacticalButton(
                        text = "REGISTER BUSINESS",
                        onClick = {
                            scope.launch {
                                val currentUserId = supabase.client.auth.currentSessionOrNull()?.user?.id
                                if (currentUserId != null) {
                                    supabase.registerBusiness(
                                        BusinessLocation(
                                            id = com.benasher44.uuid.uuid4().toString(),
                                            name = name,
                                            latitude = latitude,
                                            longitude = longitude,
                                            category = category,
                                            ownerId = currentUserId
                                        )
                                    )
                                    navigator.pop()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun CoordinateDisplay(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = TacticalTheme.shapes.medium,
        color = TacticalTheme.colors.surface.copy(alpha = 0.3f),
        border = BorderStroke(1.dp, TacticalTheme.colors.onSurface.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 8.sp, color = TacticalTheme.extraColors.MutedSlate, fontWeight = FontWeight.Bold)
            Text(if (value.length > 8) value.take(8) else value, fontSize = 12.sp, color = TacticalTheme.colors.onSurface, fontWeight = FontWeight.Black)
        }
    }
}
