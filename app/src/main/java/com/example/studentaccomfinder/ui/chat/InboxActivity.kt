package com.example.studentaccomfinder.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentaccomfinder.data.local.AppDatabase
import com.example.studentaccomfinder.databinding.ActivityInboxBinding
import com.example.studentaccomfinder.utils.SessionManager
import com.example.studentaccomfinder.viewmodel.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InboxActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInboxBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: InboxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInboxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupRecyclerView()
        observeInbox()
        
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        val currentUserId = sessionManager.getUserId()
        val userDao = AppDatabase.getDatabase(this).userDao()

        adapter = InboxAdapter(
            currentUserId = currentUserId,
            onConversationClick = { otherUserId, otherUserName ->
                openChat(otherUserId, otherUserName)
            },
            getUserInfo = { userId ->
                userDao.getUserById(userId)
            }
        )

        binding.rvInbox.layoutManager = LinearLayoutManager(this)
        binding.rvInbox.adapter = adapter
    }

    private fun observeInbox() {
        val currentUserId = sessionManager.getUserId()
        viewModel.getRecentChats(currentUserId).observe(this) { messages ->
            if (messages.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvInbox.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvInbox.visibility = View.VISIBLE
                adapter.submitList(messages)
            }
        }
    }

    private fun openChat(userId: Long, userName: String) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("RECEIVER_ID", userId)
            putExtra("RECEIVER_NAME", userName)
        }
        startActivity(intent)
    }
}
