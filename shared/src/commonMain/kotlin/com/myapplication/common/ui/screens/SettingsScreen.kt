package com.myapplication.common.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.myapplication.common.ui.components.*
import com.myapplication.common.ui.theme.TacticalTheme
import kotlinx.coroutines.launch

class SettingsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Surface(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).fillMaxWidth().height(80.dp),
                    color = Color.Transparent
                ) {
                    Row(modifier = Modifier.padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                         IconButton(onClick = { navigator.pop() }) {
                             Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TacticalTheme.colors.onBackground)
                         }
                         Spacer(Modifier.width(12.dp))
                         Text("SETTINGS", color = TacticalTheme.colors.onBackground, style = TacticalTheme.typography.h1)
                    }
                }

                val themeIsDark = com.myapplication.common.ui.theme.LocalThemeIsDark.current
                val scaffoldState = rememberScaffoldState()
                val scope = rememberCoroutineScope()

                Scaffold(
                    scaffoldState = scaffoldState,
                    backgroundColor = Color.Transparent
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp, start = 24.dp, end = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item { Text("APPEARANCE", color = TacticalTheme.extraColors.MutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) }
                        item {
                            TacticalCard(
                                modifier = Modifier.fillMaxWidth().clickable { themeIsDark.value = !themeIsDark.value }
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(if (themeIsDark.value) Icons.Default.DarkMode else Icons.Default.LightMode, null, tint = TacticalTheme.colors.primary)
                                    Spacer(Modifier.width(16.dp))
                                    Text("Dark Mode", modifier = Modifier.weight(1f), color = TacticalTheme.colors.onSurface, fontWeight = FontWeight.Bold)
                                    Switch(checked = themeIsDark.value, onCheckedChange = { themeIsDark.value = it }, colors = SwitchDefaults.colors(checkedThumbColor = TacticalTheme.colors.primary))
                                }
                            }
                        }

                        item { Spacer(Modifier.height(8.dp)) }
                        item { Text("NOTIFICATIONS", color = TacticalTheme.extraColors.MutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) }
                        item { 
                            var showNotifs by remember { mutableStateOf(true) }
                            var sounds by remember { mutableStateOf(true) }
                            TacticalCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Push Alerts", modifier = Modifier.weight(1f), color = TacticalTheme.colors.onSurface, fontSize = 14.sp)
                                        Switch(checked = showNotifs, onCheckedChange = { showNotifs = it })
                                    }
                                    Divider(color = TacticalTheme.colors.onSurface.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Alert Sounds", modifier = Modifier.weight(1f), color = TacticalTheme.colors.onSurface, fontSize = 14.sp)
                                        Switch(checked = sounds, onCheckedChange = { sounds = it })
                                    }
                                }
                            }
                        }

                        item { Spacer(Modifier.height(8.dp)) }
                        item { Text("PRIVACY & SECURITY", color = TacticalTheme.extraColors.MutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) }
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                SettingProtocolItem("Two-Factor Auth", Icons.Default.VpnKey, Color(0xFF10B981)) { scope.launch { scaffoldState.snackbarHostState.showSnackbar("MFA Portal") } }
                                SettingProtocolItem("Blocked Users", Icons.Default.Block, Color(0xFFF87171)) { scope.launch { scaffoldState.snackbarHostState.showSnackbar("Blocked list") } }
                                SettingProtocolItem("Visibility", Icons.Default.Visibility, Color(0xFFFACC15)) { scope.launch { scaffoldState.snackbarHostState.showSnackbar("Who can see me") } }
                            }
                        }
                        
                        item { Spacer(Modifier.height(8.dp)) }
                        item { Text("STORAGE & DATA", color = TacticalTheme.extraColors.MutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) }
                        item {
                            TacticalCard(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Used Storage", color = TacticalTheme.colors.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text("245 MB of 2.0 GB", color = TacticalTheme.extraColors.MutedSlate, fontSize = 11.sp)
                                    }
                                    TextButton(onClick = { scope.launch { scaffoldState.snackbarHostState.showSnackbar("Cache cleared") } }) {
                                        Text("CLEAR CACHE", color = TacticalTheme.colors.primary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }

                        item { Spacer(Modifier.height(8.dp)) }
                        item { Text("SYSTEM", color = TacticalTheme.extraColors.MutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) }
                        item { SettingProtocolItem("Language", Icons.Default.Language, Color(0xFFA78BFA)) { scope.launch { scaffoldState.snackbarHostState.showSnackbar("Language picker") } } }
                        item { SettingProtocolItem("Help & Support", Icons.Default.LiveHelp, Color(0xFF64748B)) { scope.launch { scaffoldState.snackbarHostState.showSnackbar("Support portal") } } }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingProtocolItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    TacticalCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(12.dp), color = color.copy(alpha = 0.1f)) {
                 Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(20.dp)) }
            }
            Spacer(Modifier.width(16.dp))
            Text(label, modifier = Modifier.weight(1f), color = TacticalTheme.colors.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TacticalTheme.extraColors.MutedSlate, modifier = Modifier.size(20.dp))
        }
    }
}
