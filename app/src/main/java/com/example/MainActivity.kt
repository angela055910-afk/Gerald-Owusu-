package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.screens.AddContactDialog
import com.example.ui.screens.CallScreen
import com.example.ui.screens.ChatDetailSettingsDialog
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ContactsSyncScreen
import com.example.ui.screens.EncryptionVerifyDialog
import com.example.ui.screens.LinkedDevicesScreen
import com.example.ui.screens.MainScreen
import com.example.ui.screens.NewGroupScreen
import com.example.ui.screens.ProfileEditScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatusViewerScreen
import com.example.ui.theme.JungleDarkBackground
import com.example.ui.theme.JungleTheme
import com.example.ui.viewmodel.JungleViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: JungleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JungleTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = JungleDarkBackground
                ) {
                    JungleAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun JungleAppContent(viewModel: JungleViewModel) {
    val activeChatId by viewModel.activeChatId.collectAsState()
    val activeCall by viewModel.activeCall.collectAsState()
    val activeStatusView by viewModel.activeStatusView.collectAsState()
    val isNewGroupOpen by viewModel.isNewGroupOpen.collectAsState()
    val isContactSyncOpen by viewModel.isContactSyncOpen.collectAsState()
    val isAddContactOpen by viewModel.isAddContactOpen.collectAsState()
    val isLinkedDevicesOpen by viewModel.isLinkedDevicesOpen.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val isProfileOpen by viewModel.isProfileOpen.collectAsState()
    val isRegistrationFlowActive by viewModel.isRegistrationFlowActive.collectAsState()
    val isChatSettingsOpen by viewModel.isChatSettingsOpen.collectAsState()
    val chatWallpaper by viewModel.chatWallpaper.collectAsState()
    val verificationChat by viewModel.verificationChat.collectAsState()

    val userName by viewModel.userName.collectAsState()
    val userAbout by viewModel.userAbout.collectAsState()
    val userPhone by viewModel.userPhone.collectAsState()
    val userAvatarSeed by viewModel.userAvatarSeed.collectAsState()

    val chats by viewModel.chats.collectAsState()
    val activeChatMessages by viewModel.activeChatMessages.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val aiQuickSummary by viewModel.aiQuickSummary.collectAsState()
    val contacts by viewModel.contacts.collectAsState()

    // 1. WhatsApp Registration / Onboarding Flow
    if (isRegistrationFlowActive) {
        RegisterScreen(
            onCancel = { viewModel.cancelRegistration() },
            onComplete = { name, phone, about, avatarSeed ->
                viewModel.completeRegistration(name, phone, about, avatarSeed)
            }
        )
        return
    }

    // 2. Full Screen Call Interface
    val call = activeCall
    if (call != null) {
        CallScreen(
            call = call,
            onEndCall = { viewModel.endCall() },
            onToggleMute = { viewModel.toggleMuteCall() },
            onToggleSpeaker = { viewModel.toggleSpeakerCall() },
            onToggleVideo = { viewModel.toggleVideoCall() }
        )
        return
    }

    // 3. Status Viewer Screen
    val status = activeStatusView
    if (status != null) {
        StatusViewerScreen(
            status = status,
            onClose = { viewModel.closeStatus() },
            onReply = { reply ->
                viewModel.sendMessage("Replying to status: $reply")
            }
        )
        return
    }

    // 4. Profile Editing Screen (User profile, avatar, status)
    if (isProfileOpen) {
        ProfileEditScreen(
            currentName = userName,
            currentAbout = userAbout,
            currentPhone = userPhone,
            currentAvatarSeed = userAvatarSeed,
            onClose = { viewModel.closeProfile() },
            onSaveProfile = { name, about, avatarSeed, phone ->
                viewModel.updateProfile(name, about, avatarSeed, phone)
            }
        )
        return
    }

    // 5. New Group Creation
    if (isNewGroupOpen) {
        NewGroupScreen(
            contacts = contacts,
            onClose = { viewModel.closeNewGroup() },
            onCreateGroup = { name, desc, selectedContacts, iconColorSeed ->
                viewModel.createGroup(name, desc, selectedContacts, iconColorSeed)
            }
        )
        return
    }

    // 6. Contact Sync Screen
    if (isContactSyncOpen) {
        ContactsSyncScreen(
            contacts = contacts,
            onClose = { viewModel.closeContactSync() },
            onContactSelected = { contact ->
                viewModel.closeContactSync()
                val existingChat = chats.firstOrNull { it.name == contact.name }
                if (existingChat != null) {
                    viewModel.openChat(existingChat.id)
                } else {
                    viewModel.openChat("chat_${contact.id}")
                }
            },
            onStartVoiceCall = { contact ->
                viewModel.closeContactSync()
                viewModel.startCall(contact.name, "VOICE")
            },
            onStartVideoCall = { contact ->
                viewModel.closeContactSync()
                viewModel.startCall(contact.name, "VIDEO")
            },
            onSyncNow = {
                viewModel.syncContactsNow()
            },
            onOpenNewGroup = {
                viewModel.closeContactSync()
                viewModel.openNewGroup()
            },
            onOpenAddContact = {
                viewModel.openAddContact()
            }
        )

        // Show Add Contact Dialog on top if requested from sync screen
        if (isAddContactOpen) {
            AddContactDialog(
                onDismiss = { viewModel.closeAddContact() },
                onSaveContact = { name, phone, about, startChatNow ->
                    viewModel.addNewContact(name, phone, about, startChatNow)
                }
            )
        }
        return
    }

    // 7. Linked Devices (Cross-Platform) Screen
    if (isLinkedDevicesOpen) {
        LinkedDevicesScreen(
            onClose = { viewModel.closeLinkedDevices() }
        )
        return
    }

    // 8. Settings Screen
    if (isSettingsOpen) {
        SettingsScreen(
            userName = userName,
            userAbout = userAbout,
            userPhone = userPhone,
            userAvatarSeed = userAvatarSeed,
            onOpenProfile = { viewModel.openProfile() },
            onOpenRegisterNew = { viewModel.startRegistration() },
            onClose = { viewModel.closeSettings() }
        )
        return
    }

    // 9. Active Chat Screen
    if (activeChatId != null) {
        val currentChat = chats.firstOrNull { it.id == activeChatId }
            ?: com.example.data.model.Chat(
                id = activeChatId!!,
                name = "Encrypted Contact",
                isGroup = false,
                lastMessage = "",
                lastMessageTimestamp = System.currentTimeMillis()
            )

        ChatScreen(
            chat = currentChat,
            messages = activeChatMessages,
            isAiThinking = isAiThinking,
            aiQuickSummary = aiQuickSummary,
            wallpaperHex = chatWallpaper,
            onBack = { viewModel.closeChat() },
            onSendMessage = { text, replyToId, replyToText, replyToSender ->
                viewModel.sendMessage(text, replyToId, replyToText, replyToSender)
            },
            onSendVoiceNote = { durationSec ->
                viewModel.sendVoiceNote(durationSec)
            },
            onSendAttachment = { type, name, size ->
                viewModel.sendAttachment(type, name, size)
            },
            onSendSticker = { stickerEmoji, packName ->
                viewModel.sendSticker(stickerEmoji, packName)
            },
            onReactToMessage = { message, emoji ->
                viewModel.reactToMessage(message, emoji)
            },
            onToggleStar = { messageId ->
                viewModel.toggleStar(messageId)
            },
            onDeleteMessage = { messageId ->
                viewModel.deleteMessage(messageId)
            },
            onStartCall = { contactName, callType ->
                viewModel.startCall(contactName, callType)
            },
            onVerifyEncryption = {
                viewModel.openEncryptionVerify(currentChat)
            },
            onSummarizeChat = {
                viewModel.summarizeCurrentChat()
            },
            onDismissSummary = {
                viewModel.clearQuickSummary()
            },
            onOpenChatSettings = {
                viewModel.openChatSettings()
            }
        )

        // Chat Settings & Wallpaper Dialog
        if (isChatSettingsOpen) {
            ChatDetailSettingsDialog(
                chat = currentChat,
                currentWallpaperHex = chatWallpaper,
                onDismiss = { viewModel.closeChatSettings() },
                onClearChat = { viewModel.clearCurrentChat() },
                onTogglePin = { viewModel.togglePinCurrentChat() },
                onToggleMute = { viewModel.toggleMuteCurrentChat() },
                onUpdateGroupInfo = { name, desc ->
                    viewModel.updateCurrentGroupInfo(name, desc)
                },
                onSelectWallpaper = { hex ->
                    viewModel.setChatWallpaper(hex)
                }
            )
        }

        // Safety numbers / QR verification dialog if triggered from chat
        val chatToVerify = verificationChat
        if (chatToVerify != null) {
            EncryptionVerifyDialog(
                chat = chatToVerify,
                onDismiss = { viewModel.closeEncryptionVerify() }
            )
        }
        return
    }

    // 10. Default: Main Navigation Screen (Chats, Status, Calls, EchoStream)
    MainScreen(viewModel = viewModel)

    // Add Contact Dialog when opened from MainScreen menu
    if (isAddContactOpen) {
        AddContactDialog(
            onDismiss = { viewModel.closeAddContact() },
            onSaveContact = { name, phone, about, startChatNow ->
                viewModel.addNewContact(name, phone, about, startChatNow)
            }
        )
    }

    // Verification Dialog when opened from MainScreen
    val chatToVerify = verificationChat
    if (chatToVerify != null) {
        EncryptionVerifyDialog(
            chat = chatToVerify,
            onDismiss = { viewModel.closeEncryptionVerify() }
        )
    }
}
