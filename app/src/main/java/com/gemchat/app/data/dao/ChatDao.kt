package com.gemchat.app.data.dao

import androidx.room.*
import com.gemchat.app.data.model.Conversation
import com.gemchat.app.data.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    // Conversations
    @Query("SELECT * FROM conversations ORDER BY timestamp DESC")
    fun getAllConversations(): Flow<List<Conversation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation): Long

    @Delete
    suspend fun deleteConversation(conversation: Conversation)

    @Query("UPDATE conversations SET lastMessage = :lastMessage, timestamp = :timestamp WHERE id = :id")
    suspend fun updateConversationLastMessage(id: Long, lastMessage: String, timestamp: Long)

    // Messages
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: Long): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message): Long

    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesForConversation(conversationId: Long)
}