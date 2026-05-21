package com.myapplication.common.ui.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
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
import com.myapplication.common.ui.screens.ActiveCallScreen
import com.myapplication.common.data.supabase.SupabaseService
import com.myapplication.common.ui.theme.TacticalTheme
import com.myapplication.common.ui.components.*
import org.koin.compose.koinInject

object CallTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Call)
            return remember {
                TabOptions(
                    index = 3u,
                    title = "Calls",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
        val supabase = koinInject<SupabaseService>()
        
        var selectedSubTab by remember { mutableStateOf(0) } // 0: Recent, 1: Contacts
        var searchQuery by remember { mutableStateOf("") }
        
        val callHistory = remember {
            listOf(
                CallLog("Alpha One", "12:45 PM", true, "VIDEO", "12m 4s", false),
                CallLog("Front Desk", "Yesterday", false, "STATION", "0s", true),
                CallLog("Marketing Team", "Monday", true, "VOICE", "45m 12s", false),
                CallLog("Security Node", "2 days ago", false, "STATION", "0s", true),
                CallLog("Unknown (+1 555-0199)", "3 days ago", true, "VOICE", "0s", true)
            )
        }

        val contacts = remember {
            listOf(
                Contact("Adam Jensen", "adam@mesh.net", true),
                Contact("Alpha One", "alpha@secure.com", false),
                Contact("Bethany Clark", "beth@signy.io", true),
                Contact("Charlie Mike", "charlie@uplink.net", false),
                Contact("David Sarif", "david@industries.com", true),
                Contact("Front Desk", "reception@hq.net", false)
            )
        }

        Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier.statusBarsPadding().fillMaxWidth().height(80.dp),
                    color = Color.Transparent
                ) {
                    Row(modifier = Modifier.padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("CALLS", style = TacticalTheme.typography.h1, color = TacticalTheme.colors.onBackground)
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { /* Global Search */ }) {
                            Icon(Icons.Default.Search, null, tint = TacticalTheme.colors.primary)
                        }
                    }
                }

                TabRow(
                    selectedTabIndex = selectedSubTab,
                    backgroundColor = Color.Transparent,
                    contentColor = TacticalTheme.colors.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                            color = TacticalTheme.colors.primary,
                            height = 3.dp
                        )
                    },
                    divider = { Divider(color = TacticalTheme.colors.onSurface.copy(alpha = 0.05f)) }
                ) {
                    Tab(
                        selected = selectedSubTab == 0,
                        onClick = { selectedSubTab = 0 },
                        text = { Text("RECENT", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedSubTab == 1,
                        onClick = { selectedSubTab = 1 },
                        text = { Text("CONTACTS", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }

                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                    if (selectedSubTab == 1) {
                         Spacer(Modifier.height(16.dp))
                         TacticalTextField(
                             value = searchQuery,
                             onValueChange = { searchQuery = it },
                             label = "Search contacts...",
                             modifier = Modifier.fillMaxWidth().height(52.dp)
                         )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (selectedSubTab == 0) {
                            items(callHistory) { log ->
                                CallHistoryItem(log) {
                                    rootNavigator.push(ActiveCallScreen(log.caller, log.type == "VIDEO"))
                                }
                            }
                        } else {
                            val filteredContacts = contacts.filter { it.name.contains(searchQuery, ignoreCase = true) }
                            items(filteredContacts) { contact ->
                                ContactCallItem(contact) {
                                    rootNavigator.push(ActiveCallScreen(contact.name, isVideo = false))
                                }
                            }
                        }
                    }
                }
            }

            // Dial FAB
            FloatingActionButton(
                onClick = { /* Open Dialer */ },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 96.dp),
                backgroundColor = TacticalTheme.colors.primary,
                contentColor = TacticalTheme.colors.onPrimary
            ) {
                Icon(Icons.Default.Dialpad, "Dial")
            }
        }
    }

    @Composable
    private fun CallHistoryItem(log: CallLog, onClick: () -> Unit) {
        val isMissed = log.isMissed
        TacticalCard(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                TacticalAvatar(name = log.caller, size = 44.dp)
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(log.caller, color = if(isMissed) TacticalTheme.colors.error else TacticalTheme.colors.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if(log.incoming) Icons.AutoMirrored.Filled.CallReceived else Icons.AutoMirrored.Filled.CallMade,
                            contentDescription = null,
                            tint = if(isMissed) TacticalTheme.colors.error else if(log.incoming) Color(0xFF10B981) else TacticalTheme.colors.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(log.time, color = TacticalTheme.extraColors.MutedSlate, fontSize = 11.sp)
                        if (log.duration != "0s") {
                            Text(" • ${log.duration}", color = TacticalTheme.extraColors.MutedSlate, fontSize = 11.sp)
                        }
                    }
                }
                IconButton(onClick = { onClick() }) {
                    Icon(
                        imageVector = if(log.type == "VIDEO") Icons.Default.Videocam else Icons.Default.Call,
                        contentDescription = null,
                        tint = TacticalTheme.colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun ContactCallItem(contact: Contact, onClick: () -> Unit) {
        TacticalCard(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                TacticalAvatar(name = contact.name, size = 44.dp)
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(contact.name, color = TacticalTheme.colors.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(contact.email, color = TacticalTheme.extraColors.MutedSlate, fontSize = 11.sp)
                }
                Row {
                    IconButton(onClick = { onClick() }) {
                        Icon(Icons.Default.Call, null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = { /* Navigate to Video Call */ }) {
                        Icon(Icons.Default.Videocam, null, tint = TacticalTheme.colors.primary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

data class CallLog(
    val caller: String, 
    val time: String, 
    val incoming: Boolean, 
    val type: String,
    val duration: String = "0s",
    val isMissed: Boolean = false
)

data class Contact(
    val name: String,
    val email: String,
    val isFavorite: Boolean = false
)
