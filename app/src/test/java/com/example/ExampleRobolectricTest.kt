package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.EchoStreamAiService
import com.example.data.local.JungleDatabase
import com.example.data.model.Chat
import com.example.data.model.Message
import com.example.data.repository.JungleRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Jungle", appName)
    }

    @Test
    fun `chat model creation and default values`() {
        val chat = Chat(
            id = "test_1",
            name = "Sophia Vance",
            lastMessage = "Hello",
            isEndToEndEncrypted = true
        )
        assertEquals("test_1", chat.id)
        assertEquals("Sophia Vance", chat.name)
        assertTrue(chat.isEndToEndEncrypted)
        assertEquals("S", chat.avatarInitial)
    }

    @Test
    fun `message creation with reactions and status`() {
        val message = Message(
            id = "msg_1",
            chatId = "test_1",
            senderId = "me",
            senderName = "You",
            content = "Encrypted test message",
            isOutgoing = true,
            status = "READ",
            reactions = "❤️,👍"
        )
        assertEquals("msg_1", message.id)
        assertTrue(message.isOutgoing)
        assertEquals("READ", message.status)
        assertTrue(message.reactions.contains("❤️"))
    }

    @Test
    fun `echostream ai local engine produces summary`() = runTest {
        val service = EchoStreamAiService()
        val summary = service.summarizeChat("Alice: Hello\nBob: Meeting at 3pm", "Alice")
        assertNotNull(summary)
        assertTrue(summary.isNotBlank())
    }

    @Test
    fun `add new contact and create group in repository`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = JungleDatabase.getDatabase(context)
        val repo = JungleRepository(db.chatDao())

        // 1. Add contact
        val contact = repo.addNewContact("Jordan Blake", "+1 555-0199", "Exploring Jungle")
        assertNotNull(contact)
        assertEquals("Jordan Blake", contact.name)
        assertEquals("+1 555-0199", contact.phoneNumber)

        // 2. Create group
        val groupId = repo.createGroup("Cryptographers Club", "Zero knowledge discussions", listOf("Jordan Blake", "Sophia Vance"))
        assertNotNull(groupId)
        assertTrue(groupId.startsWith("group_"))

        val chat = repo.getChat(groupId)
        assertNotNull(chat)
        assertEquals("Cryptographers Club", chat?.name)
        assertTrue(chat?.isGroup == true)
        assertEquals(3, chat?.groupMembersCount) // 2 selected + creator (You)
    }
}
