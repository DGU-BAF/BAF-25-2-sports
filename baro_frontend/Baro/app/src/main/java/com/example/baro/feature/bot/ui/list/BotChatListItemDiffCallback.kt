package com.example.baro.feature.bot.ui.list

import androidx.recyclerview.widget.DiffUtil
import com.example.baro.feature.bot.domain.model.ChatRoomSummary

class BotChatListItemDiffCallback : DiffUtil.ItemCallback<ChatRoomSummary>() {

    override fun areItemsTheSame(oldItem: ChatRoomSummary, newItem: ChatRoomSummary): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatRoomSummary, newItem: ChatRoomSummary): Boolean {
        return oldItem == newItem
    }
}
