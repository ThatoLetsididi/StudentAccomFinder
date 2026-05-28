package com.example.studentaccomfinder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.studentaccomfinder.data.local.AppDatabase
import com.example.studentaccomfinder.data.local.entity.ChatMessage
import com.example.studentaccomfinder.data.repository.ChatRepository
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ChatRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ChatRepository(database)
    }

    fun getChatHistory(user1Id: Long, user2Id: Long): LiveData<List<ChatMessage>> {
        return repository.getChatHistory(user1Id, user2Id).asLiveData()
    }

    fun sendMessage(senderId: Long, receiverId: Long, message: String) {
        viewModelScope.launch {
            val chatMessage = ChatMessage(
                senderId = senderId,
                receiverId = receiverId,
                message = message
            )
            repository.sendMessage(chatMessage)
        }
    }

    fun getRecentChats(userId: Long): LiveData<List<ChatMessage>> {
        return repository.getRecentChats(userId).asLiveData()
    }
}