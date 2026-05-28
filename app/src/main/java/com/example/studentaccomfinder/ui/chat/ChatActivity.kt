package com.example.studentaccomfinder.ui.chat

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentaccomfinder.databinding.ActivityChatBinding
import com.example.studentaccomfinder.utils.SessionManager
import com.example.studentaccomfinder.viewmodel.ChatViewModel

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: ChatAdapter
    
    private var receiverId: Long = -1
    private var receiverName: String = "Chat"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        receiverId = intent.getLongExtra("RECEIVER_ID", -1)
        receiverName = intent.getStringExtra("RECEIVER_NAME") ?: "Chat"

        if (receiverId == -1L) {
            Toast.makeText(this, "Invalid user", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        observeMessages()
    }

    private fun setupUI() {
        binding.tvChatTitle.text = receiverName
        binding.btnBack.setOnClickListener { finish() }

        val currentUserId = sessionManager.getUserId()
        adapter = ChatAdapter(currentUserId)
        binding.rvMessages.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.rvMessages.adapter = adapter

        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                viewModel.sendMessage(currentUserId, receiverId, messageText)
                binding.etMessage.setText("")
            }
        }
    }

    private fun observeMessages() {
        val currentUserId = sessionManager.getUserId()
        viewModel.getChatHistory(currentUserId, receiverId).observe(this) { messages ->
            adapter.submitList(messages)
            if (messages.isNotEmpty()) {
                binding.rvMessages.smoothScrollToPosition(messages.size - 1)
            }
        }
    }
}