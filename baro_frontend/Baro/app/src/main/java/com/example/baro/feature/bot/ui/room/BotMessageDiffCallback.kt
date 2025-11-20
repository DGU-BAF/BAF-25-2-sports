package com.example.baro.feature.bot.ui.room

import androidx.recyclerview.widget.DiffUtil
import com.example.baro.feature.bot.domain.model.ChatMessage

class BotMessageDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {

    override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem == newItem
    }
}
