package com.example.baro.feature.message.ui.room

import androidx.recyclerview.widget.DiffUtil
import com.example.baro.feature.message.domain.model.Message

class ChatMessageDiffCallback : DiffUtil.ItemCallback<Message>() {

    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}
