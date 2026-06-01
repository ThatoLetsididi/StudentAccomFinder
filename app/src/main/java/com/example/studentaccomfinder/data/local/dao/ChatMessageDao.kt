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

    /**
     * Get the most recent message for each unique conversation partner for a specific user.
     */
    @Query("""
        SELECT * FROM chat_messages 
        WHERE id IN (
            SELECT MAX(id) FROM chat_messages 
            WHERE senderId = :userId OR receiverId = :userId 
            GROUP BY CASE WHEN senderId = :userId THEN receiverId ELSE senderId END
        )
        ORDER BY timestamp DESC
    """)
    fun getRecentChats(userId: Long): Flow<List<ChatMessage>>
}