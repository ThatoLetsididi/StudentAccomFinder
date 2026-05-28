package com.example.studentaccomfinder.data.repository

import com.example.studentaccomfinder.data.local.AppDatabase
import com.example.studentaccomfinder.data.local.entity.ChatMessage
import kotlinx.coroutines.flow.Flow

class ChatRepository(database: AppDatabase) {
    private val chatMessageDao = database.chatMessageDao()

    fun getChatHistory(user1Id: Long, user2Id: Long): Flow<List<ChatMessage>> {
        return chatMessageDao.getChatHistory(user1Id, user2Id)
    }

    suspend fun sendMessage(message: ChatMessage) {
        chatMessageDao.insertMessage(message)
    }

    fun getRecentChats(userId: Long): Flow<List<ChatMessage>> {
        return chatMessageDao.getRecentChats(userId)
    }
}