package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Chat
import com.example.data.model.Message
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.JungleAvatar
import com.example.ui.components.ReadReceiptIcon
import com.example.ui.theme.EchoStreamTeal
import com.example.ui.theme.EncryptionGold
import com.example.ui.theme.JungleDarkBackground
import com.example.ui.theme.JungleDarkIncomingBubble
import com.example.ui.theme.JungleDarkOutgoingBubble
import com.example.ui.theme.JungleDarkSurface
import com.example.ui.theme.JunglePrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop

private val JungleStickers = listOf("🦁", "🐯", "🐵", "🦜", "🐼", "🐘", "🦒", "🐆", "🐊", "🦥", "🦎", "🐸", "🦋", "🦚", "🦓", "🦩")
private val PrivacyStickers = listOf("🔒", "🔐", "🛡️", "⚡", "💻", "🕵️", "🗝️", "📡", "🌐", "🚀", "🧬", "🛰️", "👁️", "⚙️", "🪙", "🧪")
private val VibesStickers = listOf("😎", "🔥", "🥳", "💖", "💯", "🍕", "☕", "🥑", "✨", "🏆", "💎", "🛸", "🌟", "🌈", "🤙", "🎉")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chat: Chat,
    messages: List<Message>,
    isAiThinking: Boolean,
    aiQuickSummary: String?,
    wallpaperHex: String = "#0B141A",
    onBack: () -> Unit,
    onSendMessage: (text: String, replyToId: String?, replyToText: String?, replyToSender: String?) -> Unit,
    onSendVoiceNote: (durationSec: Int) -> Unit,
    onSendAttachment: (type: String, name: String, size: String) -> Unit,
    onSendSticker: (stickerEmoji: String, packName: String) -> Unit,
    onReactToMessage: (Message, String) -> Unit,
    onToggleStar: (String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onStartCall: (String, String) -> Unit,
    onVerifyEncryption: () -> Unit,
    onSummarizeChat: () -> Unit,
    onDismissSummary: () -> Unit,
    onOpenChatSettings: () -> Unit
) {
    BackHandler {
        onBack()
    }

    var inputText by remember { mutableStateOf("") }
    var replyingToMessage by remember { mutableStateOf<Message?>(null) }
    var selectedMessageForMenu by remember { mutableStateOf<Message?>(null) }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showStickerTray by remember { mutableStateOf(false) }
    var selectedStickerPack by remember { mutableStateOf(0) }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var recordingSec by remember { mutableStateOf(0) }
    var isOfflineVoiceToTextActive by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Voice recording timer simulation
    LaunchedEffect(isRecordingVoice) {
        if (isRecordingVoice) {
            recordingSec = 0
            while (isRecordingVoice) {
                delay(1000)
                recordingSec++
            }
        }
    }

    // WhatsApp 2026 Feature #4: Offline Voice-to-Text simulation
    LaunchedEffect(isOfflineVoiceToTextActive) {
        if (isOfflineVoiceToTextActive) {
            val sampleVoicePhrases = listOf(
                "Hey! Testing the new WhatsApp 2026 offline voice-to-text without network.",
                "Let's schedule a call on the guest link today.",
                "Curve25519 end-to-end encrypted message received!"
            )
            val selectedPhrase = sampleVoicePhrases.random()
            for (i in 1..selectedPhrase.length) {
                if (!isOfflineVoiceToTextActive) break
                inputText = selectedPhrase.substring(0, i)
                delay(35)
            }
            delay(500)
            isOfflineVoiceToTextActive = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.clickable { onVerifyEncryption() }
                    ) {
                        JungleAvatar(
                            name = chat.name,
                            size = 38.dp,
                            colorSeed = chat.avatarColorSeed,
                            isGroup = chat.isGroup
                        )
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = chat.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    maxLines = 1
                                )
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Encrypted",
                                    tint = EncryptionGold,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Text(
                                text = if (chat.id == "chat_echostream") {
                                    if (isAiThinking) "EchoStream thinking…" else "AI Assistant • 0-Knowledge"
                                } else if (chat.isGroup) {
                                    "${chat.groupMembersCount} encrypted members"
                                } else {
                                    "online • E2EE active"
                                },
                                color = if (isAiThinking) EchoStreamTeal else Color(0xFF8696A0),
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Voice Call
                    IconButton(onClick = { onStartCall(chat.name, "VOICE") }) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Encrypted Voice Call",
                            tint = Color.White
                        )
                    }
                    // Video Call
                    IconButton(onClick = { onStartCall(chat.name, "VIDEO") }) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Encrypted Video Call",
                            tint = Color.White
                        )
                    }
                    // Overflow
                    IconButton(onClick = { showOptionsMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color.White
                        )
                    }

                    DropdownMenu(
                        expanded = showOptionsMenu,
                        onDismissRequest = { showOptionsMenu = false },
                        modifier = Modifier.background(Color(0xFF202C33))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Verify Encryption Fingerprint", color = Color.White) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, null, tint = EncryptionGold)
                            },
                            onClick = {
                                showOptionsMenu = false
                                onVerifyEncryption()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Summarize with EchoStream AI", color = EchoStreamTeal) },
                            leadingIcon = {
                                Icon(Icons.Default.AutoAwesome, null, tint = EchoStreamTeal)
                            },
                            onClick = {
                                showOptionsMenu = false
                                onSummarizeChat()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Chat & Wallpaper Options", color = JunglePrimary) },
                            leadingIcon = {
                                Icon(Icons.Default.Settings, null, tint = JunglePrimary)
                            },
                            onClick = {
                                showOptionsMenu = false
                                onOpenChatSettings()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("View Group / Contact Info", color = Color.White) },
                            onClick = {
                                showOptionsMenu = false
                                onOpenChatSettings()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JungleDarkSurface)
            )
        },
        containerColor = try { Color(android.graphics.Color.parseColor(wallpaperHex)) } catch (_: Exception) { JungleDarkBackground }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // E2EE Notice Banner
            Surface(
                color = Color(0x33FFD54F),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FFD54F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .clickable { onVerifyEncryption() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = EncryptionGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "🔒 End-to-end encrypted. Tap to verify security keys with ${chat.name}.",
                        color = Color(0xFFE9EDEF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Quick AI Summary Banner (if triggered)
            AnimatedVisibility(visible = aiQuickSummary != null) {
                Surface(
                    color = Color(0xFF1F2C34),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EchoStreamTeal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = EchoStreamTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "EchoStream AI Thread Summary",
                                    color = EchoStreamTeal,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            IconButton(
                                onClick = onDismissSummary,
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
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = aiQuickSummary ?: "",
                            color = Color(0xFFE9EDEF),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    MessageBubble(
                        message = message,
                        onReactionSelected = { emoji -> onReactToMessage(message, emoji) },
                        onReply = { replyingToMessage = message },
                        onToggleStar = { onToggleStar(message.id) },
                        onDelete = { onDeleteMessage(message.id) },
                        isGroup = chat.isGroup
                    )
                }
            }

            // AI Thinking indicator
            if (isAiThinking) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = EchoStreamTeal,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "EchoStream AI processing securely…",
                        color = EchoStreamTeal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Replying to preview bar
            if (replyingToMessage != null) {
                Surface(
                    color = Color(0xFF202C33),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Replying to ${replyingToMessage?.senderName}",
                                color = JunglePrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = replyingToMessage?.content ?: "",
                                color = Color(0xFF8696A0),
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                        IconButton(onClick = { replyingToMessage = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel reply",
                                tint = Color(0xFF8696A0),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Offline Voice-to-Text active indicator (2026 feature)
            AnimatedVisibility(visible = isOfflineVoiceToTextActive) {
                Surface(
                    color = Color(0xFF1F2C34),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JunglePrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = JunglePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Offline Voice-to-Text: Listening (No data used)",
                                color = JunglePrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = { isOfflineVoiceToTextActive = false },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color(0xFF8696A0))
                        }
                    }
                }
            }

            // Bottom Input Bar
            Surface(
                color = JungleDarkSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isRecordingVoice) {
                        // Voice recording active bar
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .background(Color(0xFF202C33), RoundedCornerShape(24.dp))
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEA4335))
                                )
                                Text(
                                    text = "0:%02d".format(recordingSec),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            AudioWaveformVisualizer(
                                isPlaying = true,
                                barCount = 14,
                                modifier = Modifier.width(90.dp)
                            )
                            IconButton(onClick = { isRecordingVoice = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancel Recording",
                                    tint = Color(0xFF8696A0),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Send Voice Note FAB
                        IconButton(
                            onClick = {
                                isRecordingVoice = false
                                onSendVoiceNote(recordingSec.coerceAtLeast(3))
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(JunglePrimary)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Voice Note",
                                tint = Color.White
                            )
                        }
                    } else {
                        // Regular message compose bar
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(Color(0xFF202C33), RoundedCornerShape(24.dp))
                                .padding(horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { /* Emoji */ }, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    imageVector = Icons.Default.SentimentSatisfiedAlt,
                                    contentDescription = "Emoji",
                                    tint = Color(0xFF8696A0),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                placeholder = {
                                    Text("Message", color = Color(0xFF8696A0), fontSize = 14.sp)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chat_message_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                maxLines = 4
                            )

                            // Attachment paperclip
                            IconButton(
                                onClick = { showAttachmentSheet = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = "Attach file or media",
                                    tint = Color(0xFF8696A0),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Sticker Tray Toggle button
                            IconButton(
                                onClick = { showStickerTray = !showStickerTray },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("sticker_toggle_button")
                            ) {
                                Text(
                                    text = if (showStickerTray) "⌨️" else "🦁",
                                    fontSize = 17.sp
                                )
                            }

                            // Quick EchoStream Sparkle button
                            IconButton(
                                onClick = {
                                    if (inputText.isBlank()) {
                                        inputText = "Summarize our latest action items"
                                    } else {
                                        inputText = "EchoStream, help refine: $inputText"
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "EchoStream AI prompt",
                                    tint = EchoStreamTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Send or Mic button
                        if (inputText.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    val text = inputText.trim()
                                    val reply = replyingToMessage
                                    inputText = ""
                                    replyingToMessage = null
                                    onSendMessage(text, reply?.id, reply?.content, reply?.senderName)
                                },
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(JunglePrimary)
                                    .testTag("send_message_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Offline Voice-to-Text Button (2026 feature)
                                IconButton(
                                    onClick = { isOfflineVoiceToTextActive = !isOfflineVoiceToTextActive },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(if (isOfflineVoiceToTextActive) Color(0xFF005C4B) else Color(0xFF202C33))
                                        .testTag("offline_voice_to_text_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = "Offline Voice to Text",
                                        tint = if (isOfflineVoiceToTextActive) JunglePrimary else Color(0xFF8696A0),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Voice recording button
                                IconButton(
                                    onClick = { isRecordingVoice = true },
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(JunglePrimary)
                                        .testTag("voice_record_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Record Voice Message",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Attachment Modal Bottom Sheet
    if (showAttachmentSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAttachmentSheet = false },
            containerColor = Color(0xFF1F2C34)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Share Encrypted Attachment",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AttachmentOptionItem(
                        icon = Icons.Default.Description,
                        label = "Document",
                        color = Color(0xFF5F66CD)
                    ) {
                        showAttachmentSheet = false
                        onSendAttachment("DOCUMENT", "Security_Specification.pdf", "3.2 MB")
                    }

                    AttachmentOptionItem(
                        icon = Icons.Default.Image,
                        label = "Gallery",
                        color = Color(0xFFAC44CF)
                    ) {
                        showAttachmentSheet = false
                        onSendAttachment("IMAGE", "encrypted_photo.jpg", "1.8 MB")
                    }

                    AttachmentOptionItem(
                        icon = Icons.Default.MyLocation,
                        label = "Location",
                        color = Color(0xFF0F9D58)
                    ) {
                        showAttachmentSheet = false
                        onSendAttachment("LOCATION", "Encrypted GPS Coordinate: 37.7749° N, 122.4194° W", "Exact")
                    }

                    AttachmentOptionItem(
                        icon = Icons.Default.Person,
                        label = "Contact",
                        color = Color(0xFF0288D1)
                    ) {
                        showAttachmentSheet = false
                        onSendAttachment("CONTACT", "Dr. Marcus Reed (+1 555-345-6789)", "VCard")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AttachmentOptionItem(
                        icon = Icons.Default.Poll,
                        label = "Poll",
                        color = Color(0xFFE9B44C)
                    ) {
                        showAttachmentSheet = false
                        onSendMessage("📊 POLL: Ready for 2026 Web Calling & Encrypted Sync?\n1️⃣ Yes, already tested!\n2️⃣ Still setting up\n3️⃣ Send invite link\n(End time: 24h • Anonymous voters)", null, null, null)
                    }

                    AttachmentOptionItem(
                        icon = Icons.Default.Videocam,
                        label = "Video Msg",
                        color = Color(0xFF26A69A)
                    ) {
                        showAttachmentSheet = false
                        onSendAttachment("VIDEO", "Instant_Circle_Video_Message.mp4", "4.1 MB")
                    }

                    AttachmentOptionItem(
                        icon = Icons.Default.GraphicEq,
                        label = "Audio",
                        color = Color(0xFFE57373)
                    ) {
                        showAttachmentSheet = false
                        onSendVoiceNote(15)
                    }

                    AttachmentOptionItem(
                        icon = Icons.Default.AutoAwesome,
                        label = "AI Sticker",
                        color = EchoStreamTeal
                    ) {
                        showAttachmentSheet = false
                        onSendSticker("🦁", "AI Jungle 2026")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: Message,
    onReactionSelected: (String) -> Unit,
    onReply: () -> Unit,
    onToggleStar: () -> Unit,
    onDelete: () -> Unit,
    isGroup: Boolean
) {
    var showMenu by remember { mutableStateOf(false) }
    var isVoicePlaying by remember { mutableStateOf(false) }

    val formattedTime = remember(message.timestamp) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date(message.timestamp))
    }

    val bubbleColor = if (message.isOutgoing) JungleDarkOutgoingBubble else JungleDarkIncomingBubble
    val bubbleShape = if (message.isOutgoing) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = if (message.isOutgoing) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            horizontalAlignment = if (message.isOutgoing) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            Surface(
                color = bubbleColor,
                shape = bubbleShape,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .clip(bubbleShape)
                    .clickable { showMenu = !showMenu }
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                    // Sender name if in group
                    if (isGroup && !message.isOutgoing) {
                        Text(
                            text = message.senderName,
                            color = EchoStreamTeal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    // Quoted Reply Preview
                    if (message.replyToText != null) {
                        Surface(
                            color = Color(0x33000000),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JunglePrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Text(
                                    text = message.replyToSender ?: "Reply",
                                    color = JunglePrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = message.replyToText,
                                    color = Color(0xFF8696A0),
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Content Rendering according to type
                    when (message.type) {
                        "VOICE" -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                IconButton(
                                    onClick = { isVoicePlaying = !isVoicePlaying },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x3300A884))
                                ) {
                                    Icon(
                                        imageVector = if (isVoicePlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Stop Voice Note",
                                        tint = JunglePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                AudioWaveformVisualizer(
                                    isPlaying = isVoicePlaying,
                                    barCount = 16,
                                    modifier = Modifier.width(110.dp)
                                )

                                Text(
                                    text = "0:%02d".format(message.voiceDurationSec ?: 12),
                                    color = Color(0xFF8696A0),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        "DOCUMENT" -> {
                            Surface(
                                color = Color(0x33000000),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = Color(0xFFE53935),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = message.mediaName ?: "Document.pdf",
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${message.mediaSize ?: "2.4 MB"} • End-to-end encrypted",
                                            color = Color(0xFF8696A0),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                        "IMAGE" -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1B272E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null,
                                        tint = JunglePrimary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Text(
                                        text = "🔒 Encrypted Photo",
                                        color = Color(0xFF8696A0),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                        "LOCATION" -> {
                            Surface(
                                color = Color(0x33000000),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MyLocation,
                                        contentDescription = null,
                                        tint = Color(0xFF0F9D58),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = message.content,
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                        else -> {
                            Text(
                                text = message.content,
                                color = Color(0xFFE9EDEF),
                                fontSize = 14.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    // Timestamp & Read Receipt
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (message.isStarred) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Starred",
                                tint = EncryptionGold,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                        Text(
                            text = formattedTime,
                            color = Color(0xFF8696A0),
                            fontSize = 10.sp
                        )
                        if (message.isOutgoing) {
                            ReadReceiptIcon(status = message.status)
                        }
                    }
                }
            }

            // Message Reactions floating badge
            if (message.reactions.isNotBlank()) {
                Surface(
                    color = Color(0xFF202C33),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3942)),
                    modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                ) {
                    Text(
                        text = message.reactions.replace(",", " "),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Quick Reaction & Action Popover
            if (showMenu) {
                Surface(
                    color = Color(0xFF202C33),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3942)),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            listOf("👍", "❤️", "😂", "😮", "😢", "🙏", "🔥").forEach { emoji ->
                                Text(
                                    text = emoji,
                                    fontSize = 18.sp,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable {
                                            onReactionSelected(emoji)
                                            showMenu = false
                                        }
                                        .padding(4.dp)
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    showMenu = false
                                    onReply()
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Reply, "Reply", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = {
                                    showMenu = false
                                    onToggleStar()
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.Star, "Star", tint = EncryptionGold, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFEA4335), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttachmentOptionItem(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = Color(0xFF8696A0),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
