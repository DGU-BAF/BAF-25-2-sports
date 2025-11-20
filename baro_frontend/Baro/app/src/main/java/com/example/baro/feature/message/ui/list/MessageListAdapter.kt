package com.example.baro.feature.message.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.baro.R
import com.example.baro.feature.message.domain.model.MessageRoomSummary
import java.time.format.DateTimeFormatter

class MessageListAdapter(
    private val onItemClick: (MessageRoomSummary) -> Unit
) : ListAdapter<MessageRoomSummary, MessageListAdapter.MessageRoomViewHolder>(
    MessageListDiffCallback()
) {

    private val timeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("HH:mm")  // 필요에 따라 수정 가능

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageRoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageRoomViewHolder(view, onItemClick, timeFormatter)
    }

    override fun onBindViewHolder(holder: MessageRoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MessageRoomViewHolder(
        itemView: View,
        private val onItemClick: (MessageRoomSummary) -> Unit,
        private val timeFormatter: DateTimeFormatter
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvChatName: TextView = itemView.findViewById(R.id.chat_name)
        private val tvLastMessage: TextView = itemView.findViewById(R.id.last_message)
        private val tvTime: TextView = itemView.findViewById(R.id.time)

        fun bind(item: MessageRoomSummary) {
            tvChatName.text = item.roomName
            tvLastMessage.text = item.lastMessage
            tvTime.text = item.lastMessageTime.format(timeFormatter)

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
