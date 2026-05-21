package com.myapplication.common.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.myapplication.common.ui.tabs.*
import com.myapplication.common.ui.theme.TacticalTheme

object DashboardScreen : Screen {
    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow
        val supabase = org.koin.compose.koinInject<com.myapplication.common.data.supabase.SupabaseService>()
        
        LaunchedEffect(Unit) {
            supabase.syncUserProfile()
        }

        TabNavigator(SecurityTab) {
            val tabNavigator = LocalTabNavigator.current
            val currentTab = tabNavigator.current

            Scaffold(
                backgroundColor = TacticalTheme.colors.background,
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    Surface(
                        modifier = Modifier.statusBarsPadding().fillMaxWidth(),
                        color = Color.Transparent,
                        elevation = 0.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = currentTab.options.title.uppercase(),
                                    style = TacticalTheme.typography.h1,
                                    color = TacticalTheme.colors.onBackground
                                )
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                    Box(modifier = Modifier.size(6.dp).background(Color(0xFF10B981), CircleShape))
                                    Spacer(Modifier.width(8.dp))
                                    Text("SYSTEM_OPERATIONAL", color = TacticalTheme.colors.primary, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                DashboardActionIcon(Icons.Default.NotificationsNone) { rootNavigator.push(NotificationHubScreen()) }
                                DashboardActionIcon(Icons.Default.Settings) { rootNavigator.push(SettingsScreen()) }
                            }
                        }
                    }
                },
                bottomBar = {
                    // Integrated Glass Navigation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(bottom = 16.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .height(80.dp),
                            color = TacticalTheme.colors.surface.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(28.dp),
                            elevation = 12.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val tabs = listOf(ChatTab, SecurityTab, ManagementTab, ProfileTab)
                                
                                // Left Tabs
                                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    tabs.take(2).forEach { tab ->
                                        DashboardTabItem(tab, tabNavigator)
                                    }
                                }
                                
                                // Center FAB Slot (Purely Integrated)
                                Box(
                                    modifier = Modifier.size(64.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    FloatingActionButton(
                                        onClick = { rootNavigator.push(DialerScreen()) },
                                        backgroundColor = TacticalTheme.colors.primary,
                                        contentColor = TacticalTheme.colors.onPrimary,
                                        elevation = FloatingActionButtonDefaults.elevation(0.dp),
                                        modifier = Modifier.size(60.dp)
                                    ) {
                                        Icon(Icons.Default.Add, null, modifier = Modifier.size(32.dp))
                                    }
                                }
                                
                                // Right Tabs
                                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    tabs.drop(2).forEach { tab ->
                                        DashboardTabItem(tab, tabNavigator)
                                    }
                                }
                            }
                        }
                    }
                }
            )
 { padding ->
                Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                    CurrentTab()
                }
            }
        }
    }
}

@Composable
fun DashboardTabItem(tab: cafe.adriel.voyager.navigator.tab.Tab, tabNavigator: cafe.adriel.voyager.navigator.tab.TabNavigator) {
    val isSelected = tabNavigator.current == tab
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ) { tabNavigator.current = tab },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = tab.options.icon!!,
            contentDescription = null,
            modifier = Modifier.size(26.dp),
            tint = if (isSelected) TacticalTheme.colors.primary else TacticalTheme.extraColors.MutedSlate.copy(alpha = 0.6f)
        )
        AnimatedVisibility(visible = isSelected) {
            Text(
                text = tab.options.title.uppercase(),
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                color = TacticalTheme.colors.primary,
                modifier = Modifier.padding(top = 4.dp),
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun DashboardActionIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(start = 8.dp)
            .size(44.dp)
            .background(TacticalTheme.colors.surface.copy(alpha = 0.5f), CircleShape)
    ) {
        Icon(icon, null, tint = TacticalTheme.colors.onBackground.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
    }
}
