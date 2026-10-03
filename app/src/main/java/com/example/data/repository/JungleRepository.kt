package com.example.data.repository

import com.example.data.local.ChatDao
import com.example.data.model.CallRecord
import com.example.data.model.Chat
import com.example.data.model.Contact
import com.example.data.model.Message
import com.example.data.model.StatusUpdate
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class JungleRepository(private val chatDao: ChatDao) {

    val allChats: Flow<List<Chat>> = chatDao.getAllChats()
    val allStatusUpdates: Flow<List<StatusUpdate>> = chatDao.getAllStatusUpdates()
    val allCallRecords: Flow<List<CallRecord>> = chatDao.getAllCallRecords()
    val allContacts: Flow<List<Contact>> = chatDao.getAllContacts()

    fun getMessages(chatId: String): Flow<List<Message>> = chatDao.getMessagesForChat(chatId)

    suspend fun getRecentMessages(chatId: String): List<Message> = chatDao.getRecentMessages(chatId)

    suspend fun getChat(chatId: String): Chat? = chatDao.getChatById(chatId)

    suspend fun sendMessage(
        chatId: String,
        content: String,
        type: String = "TEXT",
        mediaName: String? = null,
        mediaSize: String? = null,
        voiceDurationSec: Int? = null,
        replyToId: String? = null,
        replyToText: String? = null,
        replyToSender: String? = null
    ) {
        val now = System.currentTimeMillis()
        val message = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = "me",
            senderName = "You",
            content = content,
            timestamp = now,
            isOutgoing = true,
            status = "SENT",
            type = type,
            mediaName = mediaName,
            mediaSize = mediaSize,
            voiceDurationSec = voiceDurationSec,
            replyToMessageId = replyToId,
            replyToText = replyToText,
            replyToSender = replyToSender
        )
        chatDao.insertMessage(message)
        chatDao.updateLastMessage(chatId, content, now)
    }

    suspend fun sendSticker(chatId: String, stickerEmoji: String, stickerPack: String = "Jungle Wildlife") {
        val now = System.currentTimeMillis()
        val message = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = "me",
            senderName = "You",
            content = stickerEmoji,
            timestamp = now,
            isOutgoing = true,
            status = "SENT",
            type = "STICKER",
            mediaName = stickerPack
        )
        chatDao.insertMessage(message)
        chatDao.updateLastMessage(chatId, "Sticker: $stickerEmoji", now)
    }

    suspend fun clearChatMessages(chatId: String) {
        chatDao.clearMessagesForChat(chatId)
        chatDao.updateLastMessage(chatId, "Chat cleared", System.currentTimeMillis())
    }

    suspend fun togglePinChat(chatId: String) {
        chatDao.togglePinChat(chatId)
    }

    suspend fun toggleMuteChat(chatId: String) {
        chatDao.toggleMuteChat(chatId)
    }

    suspend fun updateGroupDetails(chatId: String, newName: String, newDesc: String) {
        chatDao.updateGroupDetails(chatId, newName, newDesc)
    }

    suspend fun createPoll(chatId: String, question: String, options: List<String>, isAnonymous: Boolean = false) {
        val now = System.currentTimeMillis()
        val opts = options.filter { it.isNotBlank() }.joinToString(";") { "${it.trim()}:0" }
        val content = "$question|$opts"
        val message = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = "me",
            senderName = "You",
            content = content,
            timestamp = now,
            isOutgoing = true,
            status = "SENT",
            type = "POLL",
            mediaName = if (isAnonymous) "Anonymous" else "Public"
        )
        chatDao.insertMessage(message)
        chatDao.updateLastMessage(chatId, "📊 Poll: $question", now)
    }

    suspend fun votePoll(messageId: String, optionIndex: Int) {
        val msg = chatDao.getMessageById(messageId) ?: return
        if (msg.type != "POLL") return
        val parts = msg.content.split("|")
        if (parts.size < 2) return
        val question = parts[0]
        val optionsList = parts[1].split(";").toMutableList()
        if (optionIndex in optionsList.indices) {
            val optPart = optionsList[optionIndex].split(":")
            val optTitle = optPart[0]
            val currentVotes = optPart.getOrNull(1)?.toIntOrNull() ?: 0
            optionsList[optionIndex] = "$optTitle:${currentVotes + 1}"
            val newContent = "$question|${optionsList.joinToString(";")}"
            chatDao.updateMessageContent(messageId, newContent)
        }
    }

    suspend fun editMessage(messageId: String, newContent: String) {
        chatDao.updateMessageContent(messageId, newContent)
    }

    suspend fun addIncomingMessage(
        chatId: String,
        senderName: String,
        content: String,
        type: String = "TEXT"
    ) {
        val now = System.currentTimeMillis()
        val message = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = senderName,
            senderName = senderName,
            content = content,
            timestamp = now,
            isOutgoing = false,
            status = "READ",
            type = type
        )
        chatDao.insertMessage(message)
        chatDao.updateLastMessage(chatId, content, now)
    }

    suspend fun toggleMessageReaction(message: Message, emoji: String) {
        val currentReactions = message.reactions
        val newReactions = if (currentReactions.contains(emoji)) {
            // Remove reaction
            currentReactions.replace(emoji, "").trim().replace(",,", ",").removePrefix(",").removeSuffix(",")
        } else {
            // Add or set reaction
            if (currentReactions.isBlank()) emoji else "$currentReactions,$emoji"
        }
        chatDao.updateMessageReactions(message.id, newReactions)
    }

    suspend fun toggleStar(messageId: String) {
        chatDao.toggleStarMessage(messageId)
    }

    suspend fun deleteMessage(messageId: String) {
        chatDao.deleteMessage(messageId)
    }

    suspend fun clearUnread(chatId: String) {
        chatDao.clearUnreadCount(chatId)
    }

    suspend fun createGroup(
        name: String,
        description: String,
        selectedContactNames: List<String>,
        iconColorSeed: Int = (0..5).random()
    ): String {
        val groupId = "group_" + UUID.randomUUID().toString().take(8)
        val now = System.currentTimeMillis()
        val groupChat = Chat(
            id = groupId,
            name = name,
            isGroup = true,
            groupMembersCount = selectedContactNames.size + 1,
            groupDescription = description,
            lastMessage = "You created group \"$name\"",
            lastMessageTimestamp = now,
            unreadCount = 0,
            avatarColorSeed = iconColorSeed,
            safetyFingerprint = "7190 2819 4019 2831 0192 8492 1029 4819"
        )
        chatDao.insertChat(groupChat)

        // System message
        chatDao.insertMessage(
            Message(
                id = UUID.randomUUID().toString(),
                chatId = groupId,
                senderId = "system",
                senderName = "Jungle Security",
                content = "🔒 Messages and calls are end-to-end encrypted. No one outside of this group can read or listen to them.",
                timestamp = now,
                isOutgoing = false,
                type = "TEXT"
            )
        )
        return groupId
    }

    suspend fun addNewContact(
        name: String,
        phoneNumber: String,
        statusAbout: String = "Available on Jungle"
    ): Contact {
        val contact = Contact(
            id = "c_" + UUID.randomUUID().toString().take(8),
            name = name,
            phoneNumber = phoneNumber,
            statusAbout = if (statusAbout.isBlank()) "Available on Jungle" else statusAbout,
            isJungleUser = true,
            avatarInitial = name.take(1)
        )
        chatDao.insertContact(contact)
        return contact
    }

    suspend fun createOrGetChatForContact(contact: Contact): String {
        val allChats = chatDao.getAllChatsList()
        val existingChat = allChats.firstOrNull { it.id == "chat_${contact.id}" || it.name.equals(contact.name, ignoreCase = true) }
        if (existingChat != null) {
            return existingChat.id
        }
        val chatId = "chat_" + contact.id
        val newChat = Chat(
            id = chatId,
            name = contact.name,
            isGroup = false,
            lastMessage = "Started encrypted chat",
            lastMessageTimestamp = System.currentTimeMillis(),
            unreadCount = 0,
            avatarColorSeed = (0..5).random(),
            safetyFingerprint = "8401 2947 1092 3840 9182 4710 3918 2049"
        )
        chatDao.insertChat(newChat)
        return chatId
    }

    suspend fun addStatusUpdate(content: String, colorHex: String = "#005D4B") {
        val status = StatusUpdate(
            id = UUID.randomUUID().toString(),
            contactName = "My Status",
            timestamp = System.currentTimeMillis(),
            content = content,
            backgroundColorHex = colorHex,
            mediaType = "TEXT",
            isViewed = true,
            isMine = true
        )
        chatDao.insertStatusUpdate(status)
    }

    suspend fun markStatusViewed(statusId: String) {
        chatDao.markStatusViewed(statusId)
    }

    suspend fun addCallRecord(
        contactName: String,
        callType: String,
        direction: String,
        durationSec: Int = 0
    ) {
        val record = CallRecord(
            id = UUID.randomUUID().toString(),
            contactName = contactName,
            timestamp = System.currentTimeMillis(),
            callType = callType,
            direction = direction,
            durationSec = durationSec,
            isEncrypted = true
        )
        chatDao.insertCallRecord(record)
    }

    suspend fun syncContacts() {
        val initialContacts = getInitialContacts()
        chatDao.insertContacts(initialContacts)
    }

    suspend fun populateInitialDataIfEmpty() {
        val contacts = getInitialContacts()
        chatDao.insertContacts(contacts)

        val chats = getInitialChats()
        chatDao.insertChats(chats)

        val messages = getInitialMessages()
        chatDao.insertMessages(messages)

        val statuses = getInitialStatuses()
        chatDao.insertStatusUpdates(statuses)

        val calls = getInitialCalls()
        chatDao.insertCallRecords(calls)
    }

    private fun getInitialChats(): List<Chat> {
        val now = System.currentTimeMillis()
        return listOf(
            Chat(
                id = "chat_echostream",
                name = "EchoStream AI",
                isGroup = false,
                lastMessage = "Hi! I'm your private AI assistant. Ask me to summarize or draft messages.",
                lastMessageTimestamp = now - 60000,
                unreadCount = 1,
                isPinned = true,
                avatarColorSeed = 1,
                safetyFingerprint = "9940 1823 8392 0184 7291 0384 1928 3746"
            ),
            Chat(
                id = "chat_devs",
                name = "Jungle Core Devs 🌿",
                isGroup = true,
                groupMembersCount = 14,
                groupDescription = "Development and security auditing for Jungle cross-platform apps.",
                lastMessage = "Alex: Curve25519 key exchange benchmarking completed! Sub-15ms handshake achieved.",
                lastMessageTimestamp = now - 120000,
                unreadCount = 3,
                isPinned = true,
                avatarColorSeed = 2,
                safetyFingerprint = "4810 2938 1029 4819 2039 4810 2938 4719"
            ),
            Chat(
                id = "chat_sophia",
                name = "Sophia Vance",
                isGroup = false,
                lastMessage = "🎤 Voice message (0:14)",
                lastMessageTimestamp = now - 300000,
                unreadCount = 2,
                isPinned = false,
                avatarColorSeed = 3,
                safetyFingerprint = "8401 2947 1092 3840 9182 4710 3918 2049"
            ),
            Chat(
                id = "chat_alex",
                name = "Alex Rivera",
                isGroup = false,
                lastMessage = "Thanks for the encrypted files! I'll review them now.",
                lastMessageTimestamp = now - 1800000,
                unreadCount = 0,
                isPinned = false,
                avatarColorSeed = 4,
                safetyFingerprint = "1928 3847 1029 4820 9182 3746 1928 3746"
            ),
            Chat(
                id = "chat_marcus",
                name = "Dr. Marcus Reed",
                isGroup = false,
                lastMessage = "📍 Shared location: Jungle Tech Innovation Hub",
                lastMessageTimestamp = now - 7200000,
                unreadCount = 0,
                isPinned = false,
                avatarColorSeed = 5,
                safetyFingerprint = "3746 1928 3746 8401 2947 1092 3840 9182"
            ),
            Chat(
                id = "chat_maya",
                name = "Maya Lin",
                isGroup = false,
                lastMessage = "Are we still doing the encrypted video conference at 4 PM?",
                lastMessageTimestamp = now - 14400000,
                unreadCount = 0,
                isPinned = false,
                avatarColorSeed = 0,
                safetyFingerprint = "5829 1029 4829 1029 4829 1029 4829 1029"
            )
        )
    }

    private fun getInitialMessages(): List<Message> {
        val now = System.currentTimeMillis()
        val list = mutableListOf<Message>()

        // EchoStream AI Chat messages
        list.add(
            Message(
                id = "msg_echo_1",
                chatId = "chat_echostream",
                senderId = "chat_echostream",
                senderName = "EchoStream AI",
                content = "👋 Welcome to EchoStream AI on Jungle! I am completely zero-knowledge protected. I can summarize any chat thread, draft intelligent replies, or audit your encryption fingerprint.",
                timestamp = now - 3600000,
                isOutgoing = false,
                status = "READ",
                type = "TEXT"
            )
        )
        list.add(
            Message(
                id = "msg_echo_2",
                chatId = "chat_echostream",
                senderId = "me",
                senderName = "You",
                content = "How does Jungle preserve privacy during voice and video calls?",
                timestamp = now - 1800000,
                isOutgoing = true,
                status = "READ",
                type = "TEXT"
            )
        )
        list.add(
            Message(
                id = "msg_echo_3",
                chatId = "chat_echostream",
                senderId = "chat_echostream",
                senderName = "EchoStream AI",
                content = "🔒 Every call in Jungle establishes an end-to-end encrypted peer connection via DTLS-SRTP. The encryption keys are ephemeral—generated solely on participants' devices and wiped as soon as the call ends. Jungle servers act strictly as blind relays and cannot tap or intercept audio/video streams.",
                timestamp = now - 60000,
                isOutgoing = false,
                status = "READ",
                type = "TEXT"
            )
        )

        // Sophia Vance chat
        list.add(
            Message(
                id = "msg_sophia_1",
                chatId = "chat_sophia",
                senderId = "chat_sophia",
                senderName = "Sophia Vance",
                content = "Hey! Did you check out the new Jungle update with voice call encryption?",
                timestamp = now - 1800000,
                isOutgoing = false,
                status = "READ",
                type = "TEXT"
            )
        )
        list.add(
            Message(
                id = "msg_sophia_2",
                chatId = "chat_sophia",
                senderId = "me",
                senderName = "You",
                content = "Yes! The call quality and zero-latency encryption are unbelievable.",
                timestamp = now - 1200000,
                isOutgoing = true,
                status = "READ",
                type = "TEXT"
            )
        )
        list.add(
            Message(
                id = "msg_sophia_3",
                chatId = "chat_sophia",
                senderId = "chat_sophia",
                senderName = "Sophia Vance",
                content = "Here is a quick voice note explaining the new security keys:",
                timestamp = now - 600000,
                isOutgoing = false,
                status = "READ",
                type = "VOICE",
                voiceDurationSec = 14,
                reactions = "❤️"
            )
        )
        list.add(
            Message(
                id = "msg_sophia_4",
                chatId = "chat_sophia",
                senderId = "chat_sophia",
                senderName = "Sophia Vance",
                content = "Also sending you the security architecture blueprint PDF.",
                timestamp = now - 300000,
                isOutgoing = false,
                status = "READ",
                type = "DOCUMENT",
                mediaName = "Jungle_E2EE_Protocol_v3.pdf",
                mediaSize = "2.8 MB"
            )
        )

        // Jungle Core Devs
        list.add(
            Message(
                id = "msg_devs_1",
                chatId = "chat_devs",
                senderId = "Alex Rivera",
                senderName = "Alex Rivera",
                content = "Team, we just deployed the cross-platform multi-device synchronization engine for Jungle Web & Desktop.",
                timestamp = now - 7200000,
                isOutgoing = false,
                status = "READ",
                type = "TEXT",
                reactions = "🔥,👍"
            )
        )
        list.add(
            Message(
                id = "msg_devs_2",
                chatId = "chat_devs",
                senderId = "Elena Rostova",
                senderName = "Elena Rostova",
                content = "The QR-code session pairing preserves full end-to-end forward secrecy across all connected devices.",
                timestamp = now - 3600000,
                isOutgoing = false,
                status = "READ",
                type = "TEXT"
            )
        )
        list.add(
            Message(
                id = "msg_devs_3",
                chatId = "chat_devs",
                senderId = "Alex Rivera",
                senderName = "Alex Rivera",
                content = "Alex: Curve25519 key exchange benchmarking completed! Sub-15ms handshake achieved.",
                timestamp = now - 120000,
                isOutgoing = false,
                status = "READ",
                type = "TEXT"
            )
        )

        // Alex Rivera
        list.add(
            Message(
                id = "msg_alex_1",
                chatId = "chat_alex",
                senderId = "chat_alex",
                senderName = "Alex Rivera",
                content = "Thanks for the encrypted files! I'll review them now.",
                timestamp = now - 1800000,
                isOutgoing = false,
                status = "READ",
                type = "TEXT",
                reactions = "👍"
            )
        )

        return list
    }

    private fun getInitialStatuses(): List<StatusUpdate> {
        val now = System.currentTimeMillis()
        return listOf(
            StatusUpdate(
                id = "status_mine",
                contactName = "My Status",
                timestamp = now - 1800000,
                content = "Exploring end-to-end encrypted networks on Jungle 🌿",
                backgroundColorHex = "#005D4B",
                mediaType = "TEXT",
                isViewed = true,
                isMine = true
            ),
            StatusUpdate(
                id = "status_sophia",
                contactName = "Sophia Vance",
                timestamp = now - 3600000,
                content = "Hiking in the Redwood Forest 🌲 Beautiful signals everywhere.",
                backgroundColorHex = "#0B2923",
                mediaType = "TEXT",
                isViewed = false,
                isMine = false
            ),
            StatusUpdate(
                id = "status_alex",
                contactName = "Alex Rivera",
                timestamp = now - 7200000,
                content = "Launching the EchoStream privacy protocol today! 🚀 Zero metadata leakage.",
                backgroundColorHex = "#1F2C34",
                mediaType = "TEXT",
                isViewed = false,
                isMine = false
            ),
            StatusUpdate(
                id = "status_maya",
                contactName = "Maya Lin",
                timestamp = now - 14400000,
                content = "Morning espresso + reviewing cryptographic proofs ☕",
                backgroundColorHex = "#008069",
                mediaType = "TEXT",
                isViewed = true,
                isMine = false
            )
        )
    }

    private fun getInitialCalls(): List<CallRecord> {
        val now = System.currentTimeMillis()
        return listOf(
            CallRecord(
                id = "call_1",
                contactName = "Alex Rivera",
                timestamp = now - 1800000,
                callType = "VIDEO",
                direction = "INCOMING",
                durationSec = 860,
                isEncrypted = true
            ),
            CallRecord(
                id = "call_2",
                contactName = "Sophia Vance",
                timestamp = now - 7200000,
                callType = "VOICE",
                direction = "OUTGOING",
                durationSec = 225,
                isEncrypted = true
            ),
            CallRecord(
                id = "call_3",
                contactName = "Maya Lin",
                timestamp = now - 18000000,
                callType = "VOICE",
                direction = "MISSED",
                durationSec = 0,
                isEncrypted = true
            ),
            CallRecord(
                id = "call_4",
                contactName = "Dr. Marcus Reed",
                timestamp = now - 86400000,
                callType = "VIDEO",
                direction = "INCOMING",
                durationSec = 1330,
                isEncrypted = true
            )
        )
    }

    private fun getInitialContacts(): List<Contact> {
        return listOf(
            Contact("c_1", "Alex Rivera", "+1 (555) 234-5678", "Building EchoStream AI on Jungle", true),
            Contact("c_2", "Sophia Vance", "+1 (555) 876-5432", "Privacy is not negotiable 🔒", true),
            Contact("c_3", "Dr. Marcus Reed", "+1 (555) 345-6789", "Professor of Applied Cryptography", true),
            Contact("c_4", "Maya Lin", "+1 (555) 987-6543", "Available on Jungle", true),
            Contact("c_5", "Elena Rostova", "+1 (555) 456-7890", "Distributed systems engineer", true),
            Contact("c_6", "Liam Chen", "+1 (555) 654-3210", "Zero-knowledge enthusiast", true),
            Contact("c_7", "Oliver Martinez", "+1 (555) 789-0123", "Hey there! I am using WhatsApp.", false),
            Contact("c_8", "Chloe Dubois", "+33 6 12 34 56 78", "Encrypted & off the grid", true),
            Contact("c_9", "Noah Kim", "+82 10 9876 5432", "Exploring Jungle protocol", true),
            Contact("c_10", "Zoe Washington", "+1 (555) 321-0987", "Available for audio calls", true)
        )
    }
}
