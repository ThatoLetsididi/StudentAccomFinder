package com.example.studentaccomfinder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.studentaccomfinder.data.local.entity.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Insert
    suspend fun insertMessage(message: ChatMessage)

    @Query("SELECT * FROM chat_messages WHERE (senderId = :user1Id AND receiverId = :user2Id) OR (senderId = :user2Id AND receiverId = :user1Id) ORDER BY timestamp ASC")
    fun getChatHistory(user1Id: Long, user2Id: Long): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE receiverId = :userId GROUP BY senderId ORDER BY timestamp DESC")
    fun getRecentChats(userId: Long): Flow<List<ChatMessage>>
}