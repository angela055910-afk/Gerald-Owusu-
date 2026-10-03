package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.CallRecord
import com.example.data.model.Chat
import com.example.data.model.StatusUpdate
import com.example.ui.components.EncryptionBadge
import com.example.ui.components.JungleAvatar
import com.example.ui.theme.CallEndRed
import com.example.ui.theme.EchoStreamTeal
import com.example.ui.theme.EncryptionGold
import com.example.ui.theme.JungleDarkBackground
import com.example.ui.theme.JungleDarkSurface
import com.example.ui.theme.JunglePrimary
import com.example.ui.viewmodel.JungleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: JungleViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val chats by viewModel.chats.collectAsState()
    val statusUpdates by viewModel.statusUpdates.collectAsState()
    val callRecords by viewModel.callRecords.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showAddStatusDialog by remember { mutableStateOf(false) }
    var newStatusText by remember { mutableStateOf("") }

    val filteredChats = remember(chats, searchQuery) {
        if (searchQuery.isBlank()) chats
        else chats.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.lastMessage.contains(searchQuery, ignoreCase = true)
        }
    }

    val totalUnread = remember(chats) {
        chats.sumOf { it.unreadCount }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(JungleDarkSurface)) {
                if (isSearchActive) {
                    // Search Bar Mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.toggleSearch(false) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close search",
                                tint = Color.White
                            )
                        }
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            placeholder = { Text("Search messages, contacts…", color = Color(0xFF8696A0)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("main_search_input"),
                            singleLine = true
                        )
                    }
                } else {
                    // Standard Jungle Top Bar
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Jungle",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0x2200A884))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = JunglePrimary,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Text(
                                            text = "E2EE",
                                            color = JunglePrimary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        },
                        actions = {
                            IconButton(onClick = { showAddStatusDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Camera / Status",
                                    tint = Color(0xFF8696A0)
                                )
                            }
                            IconButton(onClick = { viewModel.toggleSearch(true) }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color(0xFF8696A0)
                                )
                            }
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More options",
                                    tint = Color(0xFF8696A0)
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                modifier = Modifier.background(Color(0xFF202C33))
                            ) {
                                DropdownMenuItem(
                                    text = { Text("My Profile", color = Color.White) },
                                    leadingIcon = { Icon(Icons.Default.Person, null, tint = JunglePrimary) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.openProfile()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("New Group", color = Color.White) },
                                    leadingIcon = { Icon(Icons.Default.GroupAdd, null, tint = JunglePrimary) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.openNewGroup()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("New Contact", color = Color.White) },
                                    leadingIcon = { Icon(Icons.Default.PersonAdd, null, tint = JunglePrimary) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.openAddContact()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Linked Devices (Web/Desktop)", color = Color.White) },
                                    leadingIcon = { Icon(Icons.Default.Devices, null, tint = JunglePrimary) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.openLinkedDevices()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Synchronize Contacts", color = Color.White) },
                                    leadingIcon = { Icon(Icons.Default.ContactPage, null, tint = JunglePrimary) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.openContactSync()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("EchoStream AI Assistant", color = EchoStreamTeal) },
                                    leadingIcon = { Icon(Icons.Default.AutoAwesome, null, tint = EchoStreamTeal) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.selectTab(3)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Register / Switch Account", color = Color.White) },
                                    leadingIcon = { Icon(Icons.Default.Phone, null, tint = JunglePrimary) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.startRegistration()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Settings & Privacy", color = Color.White) },
                                    leadingIcon = { Icon(Icons.Default.Settings, null, tint = Color(0xFF8696A0)) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.openSettings()
                                    }
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = JungleDarkSurface)
                    )
                }

                // WhatsApp 4-Tab Bar
                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = JungleDarkSurface,
                    contentColor = JunglePrimary,
                    indicator = {
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(selectedTab),
                            color = JunglePrimary,
                            height = 3.dp
                        )
                    }
                ) {
                    // Tab 0: Chats
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Chats",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == 0) JunglePrimary else Color(0xFF8696A0),
                                    fontSize = 14.sp
                                )
                                if (totalUnread > 0) {
                                    Surface(
                                        color = JunglePrimary,
                                        shape = CircleShape,
                                        modifier = Modifier.size(18.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "$totalUnread",
                                                color = Color.Black,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )

                    // Tab 1: Status
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Status",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == 1) JunglePrimary else Color(0xFF8696A0),
                                    fontSize = 14.sp
                                )
                                val hasUnviewed = statusUpdates.any { !it.isViewed && !it.isMine }
                                if (hasUnviewed) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(JunglePrimary)
                                    )
                                }
                            }
                        }
                    )

                    // Tab 2: Calls
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { viewModel.selectTab(2) },
                        text = {
                            Text(
                                text = "Calls",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 2) JunglePrimary else Color(0xFF8696A0),
                                fontSize = 14.sp
                            )
                        }
                    )

                    // Tab 3: EchoStream AI
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { viewModel.selectTab(3) },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "EchoStream AI",
                                    tint = if (selectedTab == 3) EchoStreamTeal else Color(0xFF8696A0),
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "EchoStream",
                                    fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == 3) EchoStreamTeal else Color(0xFF8696A0),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            when (selectedTab) {
                0 -> {
                    FloatingActionButton(
                        onClick = { viewModel.openContactSync() },
                        containerColor = JunglePrimary,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("new_chat_fab")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "New Chat",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                1 -> {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FloatingActionButton(
                            onClick = { showAddStatusDialog = true },
                            containerColor = Color(0xFF202C33),
                            contentColor = Color.White,
                            modifier = Modifier
                                .size(46.dp)
                                .testTag("edit_status_fab")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "New Text Status",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        FloatingActionButton(
                            onClick = { showAddStatusDialog = true },
                            containerColor = JunglePrimary,
                            contentColor = Color.White,
                            modifier = Modifier.testTag("camera_status_fab")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera Status",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                2 -> {
                    FloatingActionButton(
                        onClick = { viewModel.openContactSync() },
                        containerColor = JunglePrimary,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("new_call_fab")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "New Call",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },
        containerColor = JungleDarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> ChatsTab(
                    chats = filteredChats,
                    onChatClick = { viewModel.openChat(it.id) },
                    onVerifyFingerprint = { viewModel.openEncryptionVerify(it) }
                )
                1 -> StatusTab(
                    statusUpdates = statusUpdates,
                    onOpenStatus = { viewModel.openStatus(it) },
                    onAddStatus = { showAddStatusDialog = true }
                )
                2 -> CallsTab(
                    callRecords = callRecords,
                    onStartVoiceCall = { name -> viewModel.startCall(name, "VOICE") },
                    onStartVideoCall = { name -> viewModel.startCall(name, "VIDEO") }
                )
                3 -> EchoStreamTab(
                    viewModel = viewModel
                )
            }
        }
    }

    // Add Status Dialog
    if (showAddStatusDialog) {
        var statusColor by remember { mutableStateOf("#005D4B") }
        val colorOptions = listOf("#005D4B", "#0B2923", "#1F2C34", "#7B2CBF", "#D81B60", "#E65100")

        androidx.compose.ui.window.Dialog(onDismissRequest = { showAddStatusDialog = false }) {
            Surface(
                color = Color(0xFF1F2C34),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Share Status Update",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = newStatusText,
                        onValueChange = { newStatusText = it },
                        placeholder = { Text("What's on your mind? (End-to-end encrypted)", color = Color(0xFF8696A0)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = JunglePrimary,
                            unfocusedBorderColor = Color(0xFF2A3942),
                            focusedContainerColor = Color(0xFF111B21),
                            unfocusedContainerColor = Color(0xFF111B21)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("status_text_input"),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Color picker row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        colorOptions.forEach { hex ->
                            val c = try { Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { JunglePrimary }
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(c)
                                    .border(
                                        width = if (statusColor == hex) 2.dp else 0.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                                    .clickable { statusColor = hex }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        androidx.compose.material3.TextButton(onClick = { showAddStatusDialog = false }) {
                            Text("Cancel", color = Color(0xFF8696A0))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        androidx.compose.material3.Button(
                            onClick = {
                                if (newStatusText.isNotBlank()) {
                                    viewModel.addTextStatus(newStatusText.trim(), statusColor)
                                    newStatusText = ""
                                    showAddStatusDialog = false
                                }
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                            modifier = Modifier.testTag("submit_status_button")
                        ) {
                            Text("Share Status")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatsTab(
    chats: List<Chat>,
    onChatClick: (Chat) -> Unit,
    onVerifyFingerprint: (Chat) -> Unit
) {
    var activeFilter by remember { mutableStateOf("All") }
    val filterOptions = listOf("All", "Unread", "Favorites", "Groups")

    val displayedChats = remember(chats, activeFilter) {
        when (activeFilter) {
            "Unread" -> chats.filter { it.unreadCount > 0 }
            "Favorites" -> chats.filter { it.isPinned }
            "Groups" -> chats.filter { it.isGroup }
            else -> chats
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // WhatsApp 2026 Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterOptions.forEach { filter ->
                val isSelected = activeFilter == filter
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color(0x3300A884) else Color(0xFF202C33),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, JunglePrimary) else null,
                    modifier = Modifier
                        .clickable { activeFilter = filter }
                        .testTag("filter_chip_$filter")
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) JunglePrimary else Color(0xFF8696A0),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        if (displayedChats.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (activeFilter == "All") "No messages yet.\nTap the chat button below to start encrypted conversations."
                           else "No chats match '$activeFilter' filter.",
                    color = Color(0xFF8696A0),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(displayedChats, key = { it.id }) { chat ->
                    val formattedTime = remember(chat.lastMessageTimestamp) {
                        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
                        sdf.format(Date(chat.lastMessageTimestamp))
                    }

                    Surface(
                        color = Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onChatClick(chat) }
                            .testTag("chat_item_${chat.id}")
                    ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        JungleAvatar(
                            name = chat.name,
                            size = 52.dp,
                            colorSeed = chat.avatarColorSeed,
                            isGroup = chat.isGroup
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = chat.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                if (chat.id == "chat_echostream") {
                                    Surface(
                                        color = Color(0x3300F5D4),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "AI",
                                            color = EchoStreamTeal,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = chat.lastMessage,
                                color = if (chat.unreadCount > 0) Color(0xFFE9EDEF) else Color(0xFF8696A0),
                                fontWeight = if (chat.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = formattedTime,
                            color = if (chat.unreadCount > 0) JunglePrimary else Color(0xFF8696A0),
                            fontSize = 12.sp,
                            fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (chat.isPinned) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = "Pinned",
                                    tint = Color(0xFF8696A0),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            if (chat.unreadCount > 0) {
                                Surface(
                                    color = JunglePrimary,
                                    shape = CircleShape,
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${chat.unreadCount}",
                                            color = Color.Black,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
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
}
}

@Composable
private fun StatusTab(
    statusUpdates: List<StatusUpdate>,
    onOpenStatus: (StatusUpdate) -> Unit,
    onAddStatus: () -> Unit
) {
    val myStatus = statusUpdates.firstOrNull { it.isMine }
    val otherStatuses = statusUpdates.filter { !it.isMine }
    val recentUpdates = otherStatuses.filter { !it.isViewed }
    val viewedUpdates = otherStatuses.filter { it.isViewed }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // My Status Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (myStatus != null) onOpenStatus(myStatus)
                        else onAddStatus()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    JungleAvatar(
                        name = "You",
                        size = 52.dp,
                        hasStory = myStatus != null,
                        storyViewed = false
                    )
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(JunglePrimary)
                            .border(1.5.dp, JungleDarkBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add status",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "My Status",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = if (myStatus != null) myStatus.content else "Tap to add status update",
                        color = Color(0xFF8696A0),
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
            }
        }

        // Recent Updates
        if (recentUpdates.isNotEmpty()) {
            item {
                Text(
                    text = "Recent Updates",
                    color = Color(0xFF8696A0),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(recentUpdates) { update ->
                StatusUpdateRow(update = update, onClick = { onOpenStatus(update) })
            }
        }

        // Viewed Updates
        if (viewedUpdates.isNotEmpty()) {
            item {
                Text(
                    text = "Viewed Updates",
                    color = Color(0xFF8696A0),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(viewedUpdates) { update ->
                StatusUpdateRow(update = update, onClick = { onOpenStatus(update) })
            }
        }

        // E2EE guarantee for status
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = EncryptionGold,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Your status updates are end-to-end encrypted",
                    color = Color(0xFF8696A0),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun StatusUpdateRow(
    update: StatusUpdate,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        JungleAvatar(
            name = update.contactName,
            size = 50.dp,
            hasStory = true,
            storyViewed = update.isViewed
        )
        Column {
            Text(
                text = update.contactName,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Text(
                text = "Today, End-to-End Encrypted",
                color = Color(0xFF8696A0),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun CallsTab(
    callRecords: List<CallRecord>,
    onStartVoiceCall: (String) -> Unit,
    onStartVideoCall: (String) -> Unit
) {
    var showGuestCallDialog by remember { mutableStateOf(false) }
    var guestCallType by remember { mutableStateOf("VIDEO") }
    var linkCopied by remember { mutableStateOf(false) }
    val guestCallLink = remember { "https://call.jungle.im/guest/join?room=jungle_e2ee_${System.currentTimeMillis() % 100000}" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // E2EE Info banner
        item {
            Surface(
                color = Color(0xFF111B21),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = EncryptionGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Encrypted Audio & Video: Protected by Curve25519 & DTLS-SRTP ephemeral keys.",
                        color = Color(0xFF8696A0),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // WhatsApp 2026 Feature #1: Guest Calling (Call anyone without WhatsApp)
        item {
            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showGuestCallDialog = true }
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(JunglePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Create call link",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Create call link",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Surface(
                                color = Color(0x3300A884),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "2026 GUEST CALL",
                                    color = JunglePrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Share link so anyone can join from browser without WhatsApp",
                            color = Color(0xFF8696A0),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Recent Calls",
                color = Color(0xFF8696A0),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(callRecords) { call ->
            val formattedDate = remember(call.timestamp) {
                val sdf = SimpleDateFormat("MMMM d, h:mm a", Locale.getDefault())
                sdf.format(Date(call.timestamp))
            }

            Surface(
                color = Color.Transparent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        JungleAvatar(name = call.contactName, size = 48.dp)

                        Column {
                            Text(
                                text = call.contactName,
                                color = if (call.direction == "MISSED") CallEndRed else Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                when (call.direction) {
                                    "MISSED" -> Icon(Icons.AutoMirrored.Filled.CallMissed, null, tint = CallEndRed, modifier = Modifier.size(14.dp))
                                    "INCOMING" -> Icon(Icons.AutoMirrored.Filled.CallReceived, null, tint = JunglePrimary, modifier = Modifier.size(14.dp))
                                    else -> Icon(Icons.AutoMirrored.Filled.CallMade, null, tint = JunglePrimary, modifier = Modifier.size(14.dp))
                                }
                                Text(
                                    text = formattedDate,
                                    color = Color(0xFF8696A0),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = {
                                if (call.callType == "VIDEO") onStartVideoCall(call.contactName)
                                else onStartVoiceCall(call.contactName)
                            }
                        ) {
                            Icon(
                                imageVector = if (call.callType == "VIDEO") Icons.Default.Videocam else Icons.Default.Call,
                                contentDescription = "Call back",
                                tint = JunglePrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showGuestCallDialog) {
        Dialog(onDismissRequest = {
            showGuestCallDialog = false
            linkCopied = false
        }) {
            Surface(
                color = JungleDarkSurface,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0x3300A884)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = JunglePrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Call Anyone Without App",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Send this link to anyone. They can join your encrypted call directly from Chrome, Safari, or any browser without installing WhatsApp or registering an account!",
                        color = Color(0xFF8696A0),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Call type selection (Video vs Voice)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF202C33), RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Surface(
                            color = if (guestCallType == "VIDEO") JunglePrimary else Color.Transparent,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { guestCallType = "VIDEO" }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Videocam, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Video Call", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Surface(
                            color = if (guestCallType == "VOICE") JunglePrimary else Color.Transparent,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { guestCallType = "VOICE" }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Call, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Voice Call", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Link container
                    Surface(
                        color = Color(0xFF111B21),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3942)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Guest Link ($guestCallType):",
                                color = Color(0xFF8696A0),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = guestCallLink,
                                color = JunglePrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                linkCopied = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (linkCopied) "✓ Link Copied!" else "Copy Call Link")
                        }

                        Button(
                            onClick = {
                                showGuestCallDialog = false
                                linkCopied = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A3942)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Done", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EchoStreamTab(
    viewModel: JungleViewModel
) {
    var queryText by remember { mutableStateOf("") }
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val aiResponse by viewModel.aiDialogResponse.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // EchoStream Header Card
        item {
            Surface(
                color = Color(0xFF111B21),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3300F5D4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0x3300F5D4))
                                .border(1.5.dp, EchoStreamTeal, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = EchoStreamTeal,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "EchoStream AI",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "High-Speed Privacy Intelligence",
                                color = EchoStreamTeal,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "EchoStream manages your conversations, extracts action items, and drafts context-aware replies with strict zero-knowledge encryption guarantees.",
                        color = Color(0xFF8696A0),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Quick Capability Prompt Chips
        item {
            Text(
                text = "Instant Actions",
                color = Color(0xFF8696A0),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "📋 Summarize all my unread chats and decisions",
                    "🔒 Audit my end-to-end encryption keys and security score",
                    "✍️ Draft a polite confirmation message for tomorrow's call",
                    "🌿 Explain how Jungle protects voice call metadata"
                ).forEach { prompt ->
                    Surface(
                        color = Color(0xFF202C33),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                queryText = prompt.substringAfter(" ")
                                viewModel.askEchoStreamAssistant(queryText)
                            }
                    ) {
                        Text(
                            text = prompt,
                            color = Color(0xFFE9EDEF),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        // Query Input Field
        item {
            Surface(
                color = Color(0xFF111B21),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = queryText,
                        onValueChange = { queryText = it },
                        placeholder = { Text("Ask EchoStream anything…", color = Color(0xFF8696A0), fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("echostream_prompt_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )

                    IconButton(
                        onClick = {
                            if (queryText.isNotBlank()) {
                                viewModel.askEchoStreamAssistant(queryText.trim())
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(EchoStreamTeal)
                            .testTag("send_echostream_prompt")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Ask AI",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Thinking Indicator
        if (isAiThinking) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = EchoStreamTeal,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "EchoStream processing with zero data retention…",
                        color = EchoStreamTeal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Response Card
        if (aiResponse != null) {
            item {
                Surface(
                    color = Color(0xFF1A262B),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EchoStreamTeal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "EchoStream Response",
                                color = EchoStreamTeal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            IconButton(
                                onClick = { viewModel.clearAiResponse() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = Color(0xFF8696A0),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = aiResponse ?: "",
                            color = Color(0xFFE9EDEF),
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }
    }
}
