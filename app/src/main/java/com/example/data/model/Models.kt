package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class Chat(
    @PrimaryKey val id: String,
    val name: String,
    val isGroup: Boolean = false,
    val groupMembersCount: Int = 1,
    val groupDescription: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val isEndToEndEncrypted: Boolean = true,
    val safetyFingerprint: String = "8401 2947 1092 3840 9182 4710 3918 2049 1928 3847 1029 4820",
    val avatarColorSeed: Int = 0,
    val avatarInitial: String = name.take(1)
)

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderId: String, // "me" or contact name
    val senderName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isOutgoing: Boolean,
    val status: String = "READ", // SENT, DELIVERED, READ
    val type: String = "TEXT", // TEXT, IMAGE, VOICE, DOCUMENT, LOCATION, CONTACT
    val mediaUri: String? = null,
    val mediaName: String? = null,
    val mediaSize: String? = null,
    val voiceDurationSec: Int? = null,
    val reactions: String = "", // Comma-separated reactions e.g. "❤️:1,👍:2"
    val replyToMessageId: String? = null,
    val replyToText: String? = null,
    val replyToSender: String? = null,
    val isStarred: Boolean = false,
    val isEncrypted: Boolean = true
)

@Entity(tableName = "status_updates")
data class StatusUpdate(
    @PrimaryKey val id: String,
    val contactName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val content: String,
    val backgroundColorHex: String = "#005D4B",
    val mediaType: String = "TEXT", // TEXT or IMAGE
    val isViewed: Boolean = false,
    val isMine: Boolean = false,
    val avatarInitial: String = contactName.take(1)
)

@Entity(tableName = "call_records")
data class CallRecord(
    @PrimaryKey val id: String,
    val contactName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val callType: String = "VOICE", // VOICE, VIDEO
    val direction: String = "INCOMING", // INCOMING, OUTGOING, MISSED
    val durationSec: Int = 0,
    val isEncrypted: Boolean = true,
    val avatarInitial: String = contactName.take(1)
)

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey val id: String,
    val name: String,
    val phoneNumber: String,
    val statusAbout: String = "Available on Jungle",
    val isJungleUser: Boolean = true,
    val avatarInitial: String = name.take(1)
)
