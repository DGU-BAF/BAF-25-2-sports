package com.example.baro.feature.bot.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.baro.databinding.ItemBotBinding
import com.example.baro.feature.bot.domain.model.ChatRoomSummary

class BotChatListAdapter(
    private val onRoomClicked: (ChatRoomSummary) -> Unit
) : ListAdapter<ChatRoomSummary, BotChatListAdapter.BotChatListViewHolder>(
    BotChatListItemDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BotChatListViewHolder {
        val binding = ItemBotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BotChatListViewHolder(binding, onRoomClicked)
    }

    override fun onBindViewHolder(holder: BotChatListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BotChatListViewHolder(
        private val binding: ItemBotBinding,
        private val onRoomClicked: (ChatRoomSummary) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChatRoomSummary) {
            binding.botName.text = item.title.ifBlank { "새 대화" }

            itemView.setOnClickListener {
                onRoomClicked(item)
            }
        }
    }
}
