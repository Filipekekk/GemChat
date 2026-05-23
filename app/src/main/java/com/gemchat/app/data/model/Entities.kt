package com.gemchat.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val lastMessage: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "messages",
    foreignKeys = [ForeignKey(
        entity = Conversation::class,
        parentColumns = ["id"],
        childColumns = ["conversationId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val conversationId: Long,
    val text: String,
    val isFromUser: Boolean,
    val localImagePath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)