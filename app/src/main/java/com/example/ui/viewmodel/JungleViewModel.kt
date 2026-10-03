package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.EchoStreamAiService
import com.example.data.local.JungleDatabase
import com.example.data.model.CallRecord
import com.example.data.model.Chat
import com.example.data.model.Contact
import com.example.data.model.Message
import com.example.data.model.StatusUpdate
import com.example.data.repository.JungleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActiveCallState(
    val contactName: String,
    val callType: String, // VOICE or VIDEO
    val isConnected: Boolean = false,
    val durationSec: Int = 0,
    val isMuted: Boolean = false,
    val isSpeaker: Boolean = true,
    val isVideoDisabled: Boolean = false,
    val safetyFingerprint: String = "8401 2947 1092 3840 9182 4710 3918 2049"
)

class JungleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JungleRepository
    private val aiService = EchoStreamAiService()

    init {
        val db = JungleDatabase.getDatabase(application)
        repository = JungleRepository(db.chatDao())
        viewModelScope.launch {
            repository.populateInitialDataIfEmpty()
        }
    }

    // Top-level Navigation & UI state
    private val _selectedTab = MutableStateFlow(0) // 0: Chats, 1: Status, 2: Calls, 3: EchoStream AI
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _activeChatId = MutableStateFlow<String?>(null)
    val activeChatId: StateFlow<String?> = _activeChatId.asStateFlow()

    private val _activeChatMessages = MutableStateFlow<List<Message>>(emptyList())
    val activeChatMessages: StateFlow<List<Message>> = _activeChatMessages.asStateFlow()

    private val _activeCall = MutableStateFlow<ActiveCallState?>(null)
    val activeCall: StateFlow<ActiveCallState?> = _activeCall.asStateFlow()

    private val _activeStatusView = MutableStateFlow<StatusUpdate?>(null)
    val activeStatusView: StateFlow<StatusUpdate?> = _activeStatusView.asStateFlow()

    private val _isNewGroupOpen = MutableStateFlow(false)
    val isNewGroupOpen: StateFlow<Boolean> = _isNewGroupOpen.asStateFlow()

    private val _isContactSyncOpen = MutableStateFlow(false)
    val isContactSyncOpen: StateFlow<Boolean> = _isContactSyncOpen.asStateFlow()

    private val _isAddContactOpen = MutableStateFlow(false)
    val isAddContactOpen: StateFlow<Boolean> = _isAddContactOpen.asStateFlow()

    private val _isLinkedDevicesOpen = MutableStateFlow(false)
    val isLinkedDevicesOpen: StateFlow<Boolean> = _isLinkedDevicesOpen.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    // Profile state
    private val _isProfileOpen = MutableStateFlow(false)
    val isProfileOpen: StateFlow<Boolean> = _isProfileOpen.asStateFlow()

    private val _userName = MutableStateFlow("Angela")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userAbout = MutableStateFlow("Exploring Jungle & EchoStream 🌿")
    val userAbout: StateFlow<String> = _userAbout.asStateFlow()

    private val _userPhone = MutableStateFlow("+1 (555) 789-0123")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _userAvatarSeed = MutableStateFlow(0)
    val userAvatarSeed: StateFlow<Int> = _userAvatarSeed.asStateFlow()

    // Registration flow state (WhatsApp onboarding)
    private val _isRegistrationFlowActive = MutableStateFlow(false)
    val isRegistrationFlowActive: StateFlow<Boolean> = _isRegistrationFlowActive.asStateFlow()

    // Chat Settings / Detail modal
    private val _isChatSettingsOpen = MutableStateFlow(false)
    val isChatSettingsOpen: StateFlow<Boolean> = _isChatSettingsOpen.asStateFlow()

    private val _chatWallpaper = MutableStateFlow("#0B141A")
    val chatWallpaper: StateFlow<String> = _chatWallpaper.asStateFlow()

    private val _verificationChat = MutableStateFlow<Chat?>(null)
    val verificationChat: StateFlow<Chat?> = _verificationChat.asStateFlow()

    // Search state
    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // EchoStream Assistant Dialog / Drawer / State
    private val _aiDialogResponse = MutableStateFlow<String?>(null)
    val aiDialogResponse: StateFlow<String?> = _aiDialogResponse.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _aiQuickSummary = MutableStateFlow<String?>(null)
    val aiQuickSummary: StateFlow<String?> = _aiQuickSummary.asStateFlow()

    // Streams from repository
    val chats: StateFlow<List<Chat>> = repository.allChats.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val statusUpdates: StateFlow<List<StatusUpdate>> = repository.allStatusUpdates.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val callRecords: StateFlow<List<CallRecord>> = repository.allCallRecords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val contacts: StateFlow<List<Contact>> = repository.allContacts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private var activeChatJob: Job? = null
    private var callTimerJob: Job? = null

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun toggleSearch(active: Boolean) {
        _isSearchActive.value = active
        if (!active) _searchQuery.value = ""
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openChat(chatId: String) {
        _activeChatId.value = chatId
        viewModelScope.launch {
            repository.clearUnread(chatId)
        }
        activeChatJob?.cancel()
        activeChatJob = viewModelScope.launch {
            repository.getMessages(chatId).collect {
                _activeChatMessages.value = it
            }
        }
    }

    fun closeChat() {
        _activeChatId.value = null
        activeChatJob?.cancel()
        _activeChatMessages.value = emptyList()
    }

    fun sendMessage(
        content: String,
        replyToId: String? = null,
        replyToText: String? = null,
        replyToSender: String? = null
    ) {
        val chatId = _activeChatId.value ?: return
        if (content.isBlank()) return

        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                content = content,
                type = "TEXT",
                replyToId = replyToId,
                replyToText = replyToText,
                replyToSender = replyToSender
            )

            // Auto-respond if chatting with EchoStream AI
            if (chatId == "chat_echostream") {
                _isAiThinking.value = true
                delay(800)
                val aiReply = aiService.askAssistant(content)
                repository.addIncomingMessage(
                    chatId = chatId,
                    senderName = "EchoStream AI",
                    content = aiReply,
                    type = "TEXT"
                )
                _isAiThinking.value = false
            } else if (chatId == "chat_sophia" && content.contains("?")) {
                // Interactive conversation response simulation
                delay(1200)
                repository.addIncomingMessage(
                    chatId = chatId,
                    senderName = "Sophia Vance",
                    content = "Got your message securely! All checks passed ✅",
                    type = "TEXT"
                )
            }
        }
    }

    fun sendVoiceNote(durationSec: Int = 8) {
        val chatId = _activeChatId.value ?: return
        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                content = "🎤 Voice message (0:${if (durationSec < 10) "0$durationSec" else durationSec})",
                type = "VOICE",
                voiceDurationSec = durationSec
            )
        }
    }

    fun sendAttachment(type: String, name: String, size: String) {
        val chatId = _activeChatId.value ?: return
        viewModelScope.launch {
            val content = when (type) {
                "IMAGE" -> "📷 Photo attachment"
                "DOCUMENT" -> "📄 $name"
                "LOCATION" -> "📍 Location shared: $name"
                "CONTACT" -> "👤 Contact card: $name"
                else -> "📎 Attachment: $name"
            }
            repository.sendMessage(
                chatId = chatId,
                content = content,
                type = type,
                mediaName = name,
                mediaSize = size
            )
        }
    }

    fun reactToMessage(message: Message, emoji: String) {
        viewModelScope.launch {
            repository.toggleMessageReaction(message, emoji)
        }
    }

    fun toggleStar(messageId: String) {
        viewModelScope.launch {
            repository.toggleStar(messageId)
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            repository.deleteMessage(messageId)
        }
    }

    // Call management
    fun startCall(contactName: String, callType: String) {
        callTimerJob?.cancel()
        _activeCall.value = ActiveCallState(
            contactName = contactName,
            callType = callType,
            isConnected = false,
            durationSec = 0
        )

        callTimerJob = viewModelScope.launch {
            delay(1500) // Connecting / ringing
            _activeCall.value = _activeCall.value?.copy(isConnected = true)
            var sec = 0
            while (true) {
                delay(1000)
                sec++
                _activeCall.value = _activeCall.value?.copy(durationSec = sec)
            }
        }
    }

    fun endCall() {
        val call = _activeCall.value
        callTimerJob?.cancel()
        if (call != null) {
            viewModelScope.launch {
                repository.addCallRecord(
                    contactName = call.contactName,
                    callType = call.callType,
                    direction = "OUTGOING",
                    durationSec = call.durationSec
                )
            }
        }
        _activeCall.value = null
    }

    fun toggleMuteCall() {
        val current = _activeCall.value ?: return
        _activeCall.value = current.copy(isMuted = !current.isMuted)
    }

    fun toggleSpeakerCall() {
        val current = _activeCall.value ?: return
        _activeCall.value = current.copy(isSpeaker = !current.isSpeaker)
    }

    fun toggleVideoCall() {
        val current = _activeCall.value ?: return
        _activeCall.value = current.copy(isVideoDisabled = !current.isVideoDisabled)
    }

    // Status management
    fun openStatus(status: StatusUpdate) {
        _activeStatusView.value = status
        viewModelScope.launch {
            repository.markStatusViewed(status.id)
        }
    }

    fun closeStatus() {
        _activeStatusView.value = null
    }

    fun addTextStatus(text: String, colorHex: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addStatusUpdate(text, colorHex)
        }
    }

    // Modals & Sheets
    fun openNewGroup() { _isNewGroupOpen.value = true }
    fun closeNewGroup() { _isNewGroupOpen.value = false }

    fun createGroup(name: String, description: String, selectedContacts: List<String>, iconColorSeed: Int = 0) {
        viewModelScope.launch {
            val newGroupId = repository.createGroup(name, description, selectedContacts, iconColorSeed)
            _isNewGroupOpen.value = false
            openChat(newGroupId)
        }
    }

    fun openContactSync() { _isContactSyncOpen.value = true }
    fun closeContactSync() { _isContactSyncOpen.value = false }
    fun syncContactsNow() {
        viewModelScope.launch {
            repository.syncContacts()
        }
    }

    fun openAddContact() { _isAddContactOpen.value = true }
    fun closeAddContact() { _isAddContactOpen.value = false }

    fun addNewContact(
        name: String,
        phoneNumber: String,
        statusAbout: String = "Available on Jungle",
        startChatNow: Boolean = true
    ) {
        if (name.isBlank() || phoneNumber.isBlank()) return
        viewModelScope.launch {
            val contact = repository.addNewContact(name.trim(), phoneNumber.trim(), statusAbout.trim())
            _isAddContactOpen.value = false
            if (startChatNow) {
                val chatId = repository.createOrGetChatForContact(contact)
                _isContactSyncOpen.value = false
                openChat(chatId)
            }
        }
    }

    fun openLinkedDevices() { _isLinkedDevicesOpen.value = true }
    fun closeLinkedDevices() { _isLinkedDevicesOpen.value = false }

    fun openSettings() { _isSettingsOpen.value = true }
    fun closeSettings() { _isSettingsOpen.value = false }

    // Profile actions
    fun openProfile() { _isProfileOpen.value = true }
    fun closeProfile() { _isProfileOpen.value = false }
    fun updateProfile(name: String, about: String, avatarSeed: Int, phone: String) {
        if (name.isNotBlank()) _userName.value = name.trim()
        if (about.isNotBlank()) _userAbout.value = about.trim()
        if (phone.isNotBlank()) _userPhone.value = phone.trim()
        _userAvatarSeed.value = avatarSeed
        _isProfileOpen.value = false
    }

    // Registration Flow actions
    fun startRegistration() { _isRegistrationFlowActive.value = true }
    fun cancelRegistration() { _isRegistrationFlowActive.value = false }
    fun completeRegistration(name: String, phone: String, about: String, avatarSeed: Int = 0) {
        _userName.value = name.trim()
        _userPhone.value = phone.trim()
        _userAbout.value = if (about.isBlank()) "Available on Jungle" else about.trim()
        _userAvatarSeed.value = avatarSeed
        _isRegistrationFlowActive.value = false
    }

    // Chat Settings & Wallpaper
    fun openChatSettings() { _isChatSettingsOpen.value = true }
    fun closeChatSettings() { _isChatSettingsOpen.value = false }

    fun clearCurrentChat() {
        val chatId = _activeChatId.value ?: return
        viewModelScope.launch {
            repository.clearChatMessages(chatId)
            _isChatSettingsOpen.value = false
        }
    }

    fun togglePinCurrentChat() {
        val chatId = _activeChatId.value ?: return
        viewModelScope.launch {
            repository.togglePinChat(chatId)
        }
    }

    fun toggleMuteCurrentChat() {
        val chatId = _activeChatId.value ?: return
        viewModelScope.launch {
            repository.toggleMuteChat(chatId)
        }
    }

    fun updateCurrentGroupInfo(newName: String, newDesc: String) {
        val chatId = _activeChatId.value ?: return
        viewModelScope.launch {
            repository.updateGroupDetails(chatId, newName.trim(), newDesc.trim())
            _isChatSettingsOpen.value = false
        }
    }

    fun setChatWallpaper(colorHex: String) {
        _chatWallpaper.value = colorHex
    }

    // Stickers
    fun sendSticker(stickerEmoji: String, packName: String = "Jungle Animals") {
        val chatId = _activeChatId.value ?: return
        viewModelScope.launch {
            repository.sendSticker(chatId, stickerEmoji, packName)
        }
    }

    // Polls & Message Editing
    fun createPoll(question: String, options: List<String>, isAnonymous: Boolean = false) {
        val chatId = _activeChatId.value ?: return
        if (question.isBlank()) return
        viewModelScope.launch {
            repository.createPoll(chatId, question.trim(), options, isAnonymous)
        }
    }

    fun votePoll(messageId: String, optionIndex: Int) {
        viewModelScope.launch {
            repository.votePoll(messageId, optionIndex)
        }
    }

    fun editMessage(messageId: String, newContent: String) {
        if (newContent.isBlank()) return
        viewModelScope.launch {
            repository.editMessage(messageId, newContent.trim())
        }
    }

    fun openEncryptionVerify(chat: Chat) { _verificationChat.value = chat }
    fun closeEncryptionVerify() { _verificationChat.value = null }

    // EchoStream AI Features
    fun askEchoStreamAssistant(prompt: String, context: String = "") {
        _isAiThinking.value = true
        _aiDialogResponse.value = null
        viewModelScope.launch {
            val response = aiService.askAssistant(prompt, context)
            _aiDialogResponse.value = response
            _isAiThinking.value = false
        }
    }

    fun clearAiResponse() {
        _aiDialogResponse.value = null
    }

    fun summarizeCurrentChat() {
        val chatId = _activeChatId.value ?: return
        val currentChat = chats.value.firstOrNull { it.id == chatId } ?: return
        val messages = _activeChatMessages.value
        val chatText = messages.takeLast(15).joinToString("\n") { "${it.senderName}: ${it.content}" }

        _isAiThinking.value = true
        viewModelScope.launch {
            val summary = aiService.summarizeChat(chatText, currentChat.name)
            _aiQuickSummary.value = summary
            _isAiThinking.value = false
        }
    }

    fun clearQuickSummary() {
        _aiQuickSummary.value = null
    }
}
