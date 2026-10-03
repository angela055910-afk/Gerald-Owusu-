package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CallRecord
import com.example.data.model.Chat
import com.example.data.model.Contact
import com.example.data.model.Message
import com.example.data.model.StatusUpdate
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    // --- Chats ---
    @Query("SELECT * FROM chats ORDER BY isPinned DESC, lastMessageTimestamp DESC")
    fun getAllChats(): Flow<List<Chat>>

    @Query("SELECT * FROM chats")
    suspend fun getAllChatsList(): List<Chat>

    @Query("SELECT * FROM chats WHERE id = :chatId LIMIT 1")
    suspend fun getChatById(chatId: String): Chat?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: Chat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<Chat>)

    @Update
    suspend fun updateChat(chat: Chat)

    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun deleteChatById(chatId: String)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun clearMessagesForChat(chatId: String)

    @Query("UPDATE chats SET isPinned = NOT isPinned WHERE id = :chatId")
    suspend fun togglePinChat(chatId: String)

    @Query("UPDATE chats SET isMuted = NOT isMuted WHERE id = :chatId")
    suspend fun toggleMuteChat(chatId: String)

    @Query("UPDATE chats SET name = :newName, groupDescription = :newDesc WHERE id = :chatId")
    suspend fun updateGroupDetails(chatId: String, newName: String, newDesc: String)

    @Query("UPDATE chats SET unreadCount = 0 WHERE id = :chatId")
    suspend fun clearUnreadCount(chatId: String)

    @Query("UPDATE chats SET lastMessage = :lastMsg, lastMessageTimestamp = :time WHERE id = :chatId")
    suspend fun updateLastMessage(chatId: String, lastMsg: String, time: Long)

    // --- Messages ---
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: String): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp DESC LIMIT 20")
    suspend fun getRecentMessages(chatId: String): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)

    @Update
    suspend fun updateMessage(message: Message)

    @Query("UPDATE messages SET reactions = :reactions WHERE id = :messageId")
    suspend fun updateMessageReactions(messageId: String, reactions: String)

    @Query("UPDATE messages SET isStarred = NOT isStarred WHERE id = :messageId")
    suspend fun toggleStarMessage(messageId: String)

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)

    @Query("SELECT * FROM messages WHERE id = :messageId LIMIT 1")
    suspend fun getMessageById(messageId: String): Message?

    @Query("UPDATE messages SET content = :newContent WHERE id = :messageId")
    suspend fun updateMessageContent(messageId: String, newContent: String)

    // --- Status Updates ---
    @Query("SELECT * FROM status_updates ORDER BY isMine DESC, timestamp DESC")
    fun getAllStatusUpdates(): Flow<List<StatusUpdate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatusUpdate(status: StatusUpdate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatusUpdates(statusList: List<StatusUpdate>)

    @Query("UPDATE status_updates SET isViewed = 1 WHERE id = :id")
    suspend fun markStatusViewed(id: String)

    // --- Call Records ---
    @Query("SELECT * FROM call_records ORDER BY timestamp DESC")
    fun getAllCallRecords(): Flow<List<CallRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallRecord(callRecord: CallRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallRecords(callRecords: List<CallRecord>)

    @Query("DELETE FROM call_records")
    suspend fun clearCallHistory()

    // --- Contacts ---
    @Query("SELECT * FROM contacts ORDER BY isJungleUser DESC, name ASC")
    fun getAllContacts(): Flow<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<Contact>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)
}
