package com.myapplication.common.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.myapplication.common.data.supabase.SupabaseService
import com.myapplication.common.domain.model.Message
import com.myapplication.common.ui.components.*
import com.myapplication.common.ui.theme.TacticalTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class ChatSessionScreen(
    val receiverId: String, 
    val receiverName: String,
    val isGroup: Boolean = false
) : Screen {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val supabase = koinInject<SupabaseService>()
        val scope = rememberCoroutineScope()
        var messageText by remember { mutableStateOf("") }
        val listState = rememberLazyListState()
        val permissionManager = com.myapplication.common.util.rememberBasePermissionManager()
        val currentUserId = supabase.auth.currentUserOrNull()?.id ?: ""
        
        var showGroupInfo by remember { mutableStateOf(false) }
        var isSearchMode by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }
        var isMultiSelectMode by remember { mutableStateOf(false) }
        var selectedMessages by remember { mutableStateOf(setOf<String>()) }
        var showStickers by remember { mutableStateOf(false) }
        var showAttachmentSheet by remember { mutableStateOf(false) }

        val messagesState = produceState(initialValue = emptyList<Message>()) {
            supabase.observeMessages(receiverId).collect {
                value = it
            }
        }
        val messages = messagesState.value

        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(TacticalTheme.colors.background).imePadding()) {
             // Atmosphere
            Box(
                modifier = Modifier
                    .size(400.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 100.dp, y = (-50).dp)
                    .alpha(0.05f)
                    .blur(100.dp)
                    .background(Brush.radialGradient(listOf(TacticalTheme.colors.primary, Color.Transparent)), CircleShape)
            )

            Column(modifier = Modifier.fillMaxSize()) {
                // Header Area
                Surface(
                    modifier = Modifier.statusBarsPadding().fillMaxWidth().height(72.dp),
                    color = TacticalTheme.colors.surface.copy(alpha = 0.8f),
                    elevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp), 
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isMultiSelectMode) {
                            IconButton(onClick = { isMultiSelectMode = false; selectedMessages = emptySet() }) { Icon(Icons.Default.Close, null, tint = TacticalTheme.colors.onSurface) }
                            Spacer(Modifier.width(16.dp))
                            Text("${selectedMessages.size} SELECTED", color = TacticalTheme.colors.onSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { /* COPY LOGIC */ }) { Icon(Icons.Default.ContentCopy, null, tint = TacticalTheme.colors.onSurface) }
                            IconButton(onClick = { /* DELETE LOGIC */ }) { Icon(Icons.Default.Delete, null, tint = TacticalTheme.colors.error) }
                        } else if (isSearchMode) {
                            IconButton(onClick = { isSearchMode = false; searchQuery = "" }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TacticalTheme.colors.onSurface) }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                textStyle = androidx.compose.ui.text.TextStyle(color = TacticalTheme.colors.onSurface, fontSize = 14.sp),
                                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                                cursorBrush = SolidColor(TacticalTheme.colors.primary)
                            )
                        } else {
                            IconButton(onClick = { navigator.pop() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TacticalTheme.colors.onSurface) }
                            
                            Row(
                                modifier = Modifier.weight(1f).clickable { if (isGroup) showGroupInfo = true else navigator.push(OtherProfileScreen(receiverId, receiverName)) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TacticalAvatar(name = receiverName, size = 40.dp)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(receiverName, color = TacticalTheme.colors.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(if(isGroup) "Active Group" else "Last seen recently", color = TacticalTheme.colors.primary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            IconButton(onClick = { isSearchMode = true }) { Icon(Icons.Default.Search, null, tint = TacticalTheme.colors.onSurface) }
                            IconButton(onClick = { 
                                scope.launch {
                                    if (permissionManager.hasMicrophonePermission()) {
                                        navigator.push(ActiveCallScreen(receiverName, isVideo = false, meetingId = if(isGroup) receiverId else null)) 
                                    }
                                }
                            }) { Icon(Icons.Default.Call, null, tint = TacticalTheme.colors.onSurface) }
                            IconButton(onClick = { 
                                scope.launch {
                                    if (permissionManager.hasCameraPermission() && permissionManager.hasMicrophonePermission()) {
                                        navigator.push(ActiveCallScreen(receiverName, isVideo = true, meetingId = if(isGroup) receiverId else null))
                                    }
                                }
                            }) { Icon(Icons.Default.Videocam, null, tint = TacticalTheme.colors.onSurface) }
                        }
                    }
                }

                // CHAT FEED
                Box(modifier = Modifier.weight(1f)) {
                    val filteredMessages = if(isSearchMode) messages.filter { it.text.contains(searchQuery, ignoreCase = true) } else messages
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredMessages) { message ->
                            val isMe = message.senderId == currentUserId
                            val isSelected = selectedMessages.contains(message.id)
                            ChatBubbleNode(
                                message = message, 
                                isMe = isMe,
                                isSelected = isSelected,
                                onLongClick = { 
                                    isMultiSelectMode = true
                                    selectedMessages = selectedMessages + message.id
                                },
                                onClick = {
                                    if (isMultiSelectMode) {
                                        selectedMessages = if(isSelected) selectedMessages - message.id else selectedMessages + message.id
                                        if (selectedMessages.isEmpty()) isMultiSelectMode = false
                                    }
                                }
                            )
                        }
                    }
                }

                // COMMAND INPUT
                Surface(
                    color = TacticalTheme.colors.surface.copy(alpha = 0.95f), 
                    elevation = 20.dp,
                    border = BorderStroke(1.dp, TacticalTheme.colors.onSurface.copy(alpha = 0.05f))
                ) {
                    Column {
                        if (showStickers) {
                            StickerMatrix { sticker ->
                                scope.launch { supabase.sendMessage("sticker:$sticker", receiverId) }
                                showStickers = false
                            }
                        }
                        
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { showStickers = !showStickers }) { 
                                Icon(Icons.Default.Face, null, tint = if(showStickers) TacticalTheme.colors.primary else TacticalTheme.colors.onSurface) 
                            }
                            
                            Surface(
                                modifier = Modifier.weight(1f).height(48.dp), 
                                shape = RoundedCornerShape(24.dp), 
                                color = TacticalTheme.colors.background.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, TacticalTheme.colors.onSurface.copy(alpha = 0.08f))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        if (messageText.isEmpty()) Text("Type a message...", color = TacticalTheme.extraColors.MutedSlate, fontSize = 13.sp)
                                        BasicTextField(
                                            value = messageText,
                                            onValueChange = { messageText = it },
                                            textStyle = androidx.compose.ui.text.TextStyle(color = TacticalTheme.colors.onSurface, fontSize = 13.sp),
                                            modifier = Modifier.fillMaxWidth(),
                                            cursorBrush = SolidColor(TacticalTheme.colors.primary)
                                        )
                                    }
                                    IconButton(onClick = { showAttachmentSheet = true }) { 
                                        Icon(Icons.Default.AttachFile, null, tint = TacticalTheme.colors.primary) 
                                    }
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Surface(
                                modifier = Modifier.size(48.dp).clickable { 
                                    if(messageText.isNotEmpty()) {
                                        scope.launch {
                                            supabase.sendMessage(messageText, receiverId)
                                            messageText = ""
                                        }
                                    }
                                }, 
                                shape = CircleShape, 
                                color = TacticalTheme.colors.primary,
                                elevation = 8.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) { 
                                    Icon(Icons.AutoMirrored.Filled.Send, null, tint = TacticalTheme.colors.onPrimary, modifier = Modifier.size(20.dp)) 
                                }
                            }
                        }
                    }
                }
            }

            // ATTACHMENT SHEET
            if (showAttachmentSheet) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)).clickable { showAttachmentSheet = false })
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(), 
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp), 
                    color = TacticalTheme.colors.surface
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                         Text("ATTACHMENT", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                         Spacer(Modifier.height(28.dp))
                         Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                             AttachmentIcon(Icons.Default.PhotoCamera, "CAMERA", TacticalTheme.colors.primary) { showAttachmentSheet = false }
                             AttachmentIcon(Icons.Default.Photo, "GALLERY", TacticalTheme.colors.primary) { showAttachmentSheet = false }
                             AttachmentIcon(Icons.Default.Description, "FILE", Color(0xFF818CF8)) { showAttachmentSheet = false }
                             AttachmentIcon(Icons.Default.Person, "CONTACT", Color(0xFF10B981)) { showAttachmentSheet = false }
                             AttachmentIcon(Icons.Default.LocationOn, "LOCATION", Color(0xFFF472B6)) { showAttachmentSheet = false }
                         }
                         Spacer(Modifier.height(32.dp))
                    }
                }
            }

            // GROUP INFO
            if (showGroupInfo && isGroup) {
                Surface(modifier = Modifier.fillMaxSize(), color = TacticalTheme.colors.background.copy(alpha = 0.95f)) {
                    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                           IconButton(onClick = { showGroupInfo = false }) { Icon(Icons.Default.Close, null, tint = TacticalTheme.colors.error) }
                           Spacer(Modifier.width(16.dp))
                           Text("GROUP INFORMATION", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        
                        Spacer(Modifier.height(40.dp))
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                            TacticalAvatar(name = receiverName, size = 120.dp)
                        }
                        Spacer(Modifier.height(24.dp))
                        Text(receiverName.uppercase(), modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        Text("Secure Group Channel", modifier = Modifier.align(Alignment.CenterHorizontally), color = TacticalTheme.colors.primary, fontSize = 12.sp)
                        
                        Spacer(Modifier.height(48.dp))
                        Text("MEMBERS", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        Spacer(Modifier.height(16.dp))
                        
                        val members = listOf(Member("ADMIN ALPHA", "Owner", true), Member("OPERATOR RED", "Member", false))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(members) { member ->
                                TacticalCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                         TacticalAvatar(name = member.name, size = 44.dp)
                                         Spacer(Modifier.width(16.dp))
                                         Column(modifier = Modifier.weight(1f)) {
                                             Text(member.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                             Text(member.role, color = TacticalTheme.extraColors.MutedSlate, fontSize = 11.sp)
                                         }
                                         if (member.isOnline) Surface(modifier = Modifier.size(10.dp), shape = CircleShape, color = Color(0xFF10B981)) {}
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Member(val name: String, val role: String, val isOnline: Boolean)

@Composable
fun AttachmentIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = color.copy(alpha = 0.15f), border = BorderStroke(1.dp, color.copy(alpha = 0.3f))) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(24.dp)) }
        }
        Spacer(Modifier.height(12.dp))
        Text(label, color = TacticalTheme.extraColors.MutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StickerMatrix(onStickerSelected: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(200.dp), 
        color = TacticalTheme.colors.surface,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        LazyRow(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(10) { index ->
                Surface(
                    modifier = Modifier.size(80.dp).clickable { onStickerSelected("ST_0$index") }, 
                    shape = RoundedCornerShape(16.dp), 
                    color = Color.White.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Box(contentAlignment = Alignment.Center) { Text("STICKER $index", color = Color.White, fontSize = 11.sp) }
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ChatBubbleNode(message: Message, isMe: Boolean, isSelected: Boolean = false, onLongClick: () -> Unit = {}, onClick: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().combinedClickable(onLongClick = onLongClick, onClick = onClick), horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start) {
        Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp, 
                    topEnd = 16.dp, 
                    bottomStart = if(isMe) 16.dp else 4.dp, 
                    bottomEnd = if(isMe) 4.dp else 16.dp
                ),
                color = if (isSelected) TacticalTheme.colors.primary.copy(alpha = 0.4f) 
                        else if(isMe) TacticalTheme.colors.primary 
                        else TacticalTheme.colors.surface,
                elevation = 4.dp,
                border = if (isSelected) BorderStroke(1.dp, TacticalTheme.colors.primary) else null
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Text(
                        text = message.text, 
                        color = if(isMe && !isSelected) TacticalTheme.colors.onPrimary else TacticalTheme.colors.onSurface, 
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(modifier = Modifier.align(Alignment.End), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "JUST NOW", 
                            color = if(isMe && !isSelected) TacticalTheme.colors.onPrimary.copy(alpha = 0.6f) else TacticalTheme.extraColors.MutedSlate, 
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isMe) {
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.DoneAll, 
                                contentDescription = null, 
                                tint = if (isSelected) TacticalTheme.colors.onPrimary else if(isMe) TacticalTheme.colors.onPrimary.copy(alpha = 0.7f) else TacticalTheme.colors.primary, 
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
