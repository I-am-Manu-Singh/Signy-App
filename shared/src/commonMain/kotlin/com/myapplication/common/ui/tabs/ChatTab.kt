package com.myapplication.common.ui.tabs

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.myapplication.common.data.supabase.SupabaseService
import com.myapplication.common.domain.model.User
import com.myapplication.common.ui.screens.ChatSessionScreen
import com.myapplication.common.ui.components.*
import com.myapplication.common.ui.theme.TacticalTheme
import io.github.jan.supabase.postgrest.postgrest
import org.koin.compose.koinInject

object ChatTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.AutoMirrored.Filled.Chat)
            return remember {
                TabOptions(
                    index = 1u,
                    title = "Messages",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
        val supabase = koinInject<SupabaseService>()
        
        var searchQuery by remember { mutableStateOf("") }
        var selectedTabIndex by remember { mutableStateOf(0) }

        val usersState = produceState(initialValue = emptyList<User>()) {
            try {
                value = supabase.client.postgrest["profiles"].select().decodeList<User>()
            } catch (e: Exception) {
                println("Identity Fetch Error: ${e.message}")
            }
        }
        val users = usersState.value.filter { it.name.contains(searchQuery, ignoreCase = true) }

        Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Area
                Surface(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).fillMaxWidth().height(100.dp),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MESSAGES", 
                            style = TacticalTheme.typography.h1,
                            color = TacticalTheme.colors.onBackground
                        )
                    }
                }

                // Search Bar
                TacticalCard(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, null, tint = TacticalTheme.colors.primary.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        TacticalTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = "Search people...",
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Custom Tab Row
                val tabs = listOf("PEOPLE", "GROUPS")
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTabIndex == index
                        Surface(
                            modifier = Modifier.clickable { selectedTabIndex = index },
                            shape = CircleShape,
                            color = if (isSelected) TacticalTheme.colors.primary.copy(alpha = 0.15f) else Color.Transparent,
                            border = if (isSelected) BorderStroke(1.dp, TacticalTheme.colors.primary.copy(alpha = 0.3f)) else null
                        ) {
                            Text(
                                text = title, 
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                                fontSize = 11.sp, 
                                fontWeight = FontWeight.Bold,
                                color = if(isSelected) TacticalTheme.colors.primary else TacticalTheme.extraColors.MutedSlate,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (selectedTabIndex == 0) {
                        item { Text("CONTACTS", color = TacticalTheme.extraColors.MutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) }
                        
                        if (users.isEmpty()) {
                            item { EmptyIdentityGrid() }
                        } else {
                            items(users) { contact ->
                                ContactItemNode(contact) {
                                    if (rootNavigator.lastItem !is ChatSessionScreen) {
                                        rootNavigator.push(ChatSessionScreen(contact.id, contact.name))
                                    }
                                }
                            }
                        }
                    } else {
                        item { Text("GROUPS", color = TacticalTheme.extraColors.MutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) }
                        
                        val groups = listOf("ALPHA SQUAD", "SIGINT TEAM", "OPERATIONS")
                        items(groups) { group ->
                            GroupItemNode(group) {
                                rootNavigator.push(ChatSessionScreen(group, group, isGroup = true)) 
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactItemNode(user: User, onClick: () -> Unit) {
    TacticalCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            TacticalAvatar(name = user.name, avatarUrl = user.avatar, size = 48.dp)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.name, color = TacticalTheme.colors.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.Default.Verified, null, tint = TacticalTheme.colors.primary, modifier = Modifier.size(12.dp))
                }
                Text("Last seen recently", color = TacticalTheme.extraColors.MutedSlate, fontSize = 11.sp)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TacticalTheme.extraColors.MutedSlate, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun GroupItemNode(group: String, onClick: () -> Unit) {
    TacticalCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(TacticalTheme.colors.primary.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Groups, null, tint = TacticalTheme.colors.primary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(group, color = TacticalTheme.colors.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Active channel", color = TacticalTheme.colors.secondary, fontSize = 11.sp)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TacticalTheme.extraColors.MutedSlate, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun EmptyIdentityGrid() {
    Column(modifier = Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.SearchOff, null, tint = TacticalTheme.colors.surface, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(16.dp))
        Text("NO CONTACTS FOUND", color = TacticalTheme.extraColors.MutedSlate, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
