package com.example.baro.feature.message.ui.list

import androidx.recyclerview.widget.DiffUtil
import com.example.baro.feature.message.domain.model.MessageRoomSummary

class MessageListDiffCallback : DiffUtil.ItemCallback<MessageRoomSummary>() {

    override fun areItemsTheSame(
        oldItem: MessageRoomSummary,
        newItem: MessageRoomSummary
    ): Boolean {
        return oldItem.roomId == newItem.roomId
    }

    override fun areContentsTheSame(
        oldItem: MessageRoomSummary,
        newItem: MessageRoomSummary
    ): Boolean {
        return oldItem == newItem
    }
}
