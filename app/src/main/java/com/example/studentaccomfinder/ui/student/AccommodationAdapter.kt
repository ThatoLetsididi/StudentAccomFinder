package com.example.studentaccomfinder.ui.student

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studentaccomfinder.data.local.entity.Accommodation
import com.example.studentaccomfinder.databinding.ItemAccommodationBinding
import com.example.studentaccomfinder.utils.GlideHelper
import java.text.NumberFormat
import java.util.Locale

/**
 * AccommodationAdapter — RecyclerView adapter for accommodation listings
 */
class AccommodationAdapter(
    private val onItemClick: (Accommodation) -> Unit,
    private val onChatClick: (Accommodation) -> Unit
) : ListAdapter<Accommodation, AccommodationAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAccommodationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemAccommodationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Set click listener on the entire card
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }

            // Set click listener on the chat button
            binding.btnChat.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onChatClick(getItem(position))
                }
            }
        }

        fun bind(accommodation: Accommodation) {
            // Load image using Glide
            GlideHelper.loadAccommodationImage(
                binding.root.context,
                binding.ivImage,
                accommodation.imageName
            )

            // Set text fields
            binding.tvTitle.text = accommodation.title
            binding.tvLocation.text = accommodation.location
            binding.tvType.text = accommodation.type

            // Format price with BWP currency
            val formatter = NumberFormat.getCurrencyInstance(Locale("en", "BW"))
            binding.tvPrice.text = formatter.format(accommodation.price)

            // Status badge
            binding.tvStatus.text = accommodation.status
            val statusColor = if (accommodation.status == "AVAILABLE") {
                android.graphics.Color.parseColor("#4CAF50") // Green
            } else {
                android.graphics.Color.parseColor("#F44336") // Red
            }
            binding.tvStatus.setBackgroundColor(statusColor)
        }
    }

    /**
     * DiffUtil callback — tells ListAdapter how to compare items
     */
    class DiffCallback : DiffUtil.ItemCallback<Accommodation>() {
        override fun areItemsTheSame(oldItem: Accommodation, newItem: Accommodation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Accommodation, newItem: Accommodation): Boolean {
            return oldItem == newItem
        }
    }
}