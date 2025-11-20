package com.example.baro.feature.bot.ui.room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.baro.databinding.ItemBotMessageBotBinding
import com.example.baro.databinding.ItemBotMessageUserBinding
import com.example.baro.feature.bot.domain.model.ChatMessage
import com.example.baro.feature.bot.domain.model.SenderType

class BotMessageAdapter :
    ListAdapter<ChatMessage, RecyclerView.ViewHolder>(BotMessageDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_BOT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).sender) {
            SenderType.USER -> VIEW_TYPE_USER
            SenderType.BOT -> VIEW_TYPE_BOT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val binding = ItemBotMessageUserBinding.inflate(inflater, parent, false)
                UserMessageViewHolder(binding)
            }
            VIEW_TYPE_BOT -> {
                val binding = ItemBotMessageBotBinding.inflate(inflater, parent, false)
                BotMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is UserMessageViewHolder -> holder.bind(message)
            is BotMessageViewHolder -> holder.bind(message)
        }
    }

    class UserMessageViewHolder(
        private val binding: ItemBotMessageUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.tvUserMessage.text = message.text
        }
    }

    class BotMessageViewHolder(
        private val binding: ItemBotMessageBotBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.tvBotMessage.text = message.text
        }
    }
}
