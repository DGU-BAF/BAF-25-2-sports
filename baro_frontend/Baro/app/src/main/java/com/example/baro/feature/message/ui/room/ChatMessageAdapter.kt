package com.example.baro.feature.message.ui.room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.baro.R
import com.example.baro.feature.message.domain.model.Message
import java.time.format.DateTimeFormatter

class ChatMessageAdapter :
    ListAdapter<Message, RecyclerView.ViewHolder>(ChatMessageDiffCallback()) {

    private val timeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("HH:mm") // 필요에 따라 수정 가능

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.isMine) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val view = inflater.inflate(R.layout.item_message_sent, parent, false)
                SentMessageViewHolder(view, timeFormatter)
            }
            VIEW_TYPE_RECEIVED -> {
                val view = inflater.inflate(R.layout.item_message_received, parent, false)
                ReceivedMessageViewHolder(view, timeFormatter)
            }
            else -> error("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
        }
    }

    class SentMessageViewHolder(
        itemView: View,
        private val timeFormatter: DateTimeFormatter
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(message: Message) {
            tvContent.text = message.content
            tvTime.text = message.createdAt.format(timeFormatter)
        }
    }

    class ReceivedMessageViewHolder(
        itemView: View,
        private val timeFormatter: DateTimeFormatter
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvSender: TextView = itemView.findViewById(R.id.tvSender)
        private val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(message: Message) {
            tvSender.text = message.senderName
            tvContent.text = message.content
            tvTime.text = message.createdAt.format(timeFormatter)
        }
    }

    private companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }
}
