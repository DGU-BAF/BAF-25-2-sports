package com.example.baro.feature.party.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.baro.databinding.ItemPartyBinding

class PartyListAdapter(
    private val onClickItem: (String) -> Unit,
    private val onClickAction: (PartyListItemUiModel) -> Unit
) : RecyclerView.Adapter<PartyListAdapter.PartyViewHolder>() {

    private val items = mutableListOf<PartyListItemUiModel>()

    fun submitList(list: List<PartyListItemUiModel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class PartyViewHolder(
        private val binding: ItemPartyBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PartyListItemUiModel) = with(binding) {

            val data = item.summary

            title.text = data.title
            sport.text = data.sport
            time.text = "${data.startTime} ~ ${data.endTime}"


            btnAction.text = item.actionText
            btnAction.isEnabled = item.actionEnabled

            root.setOnClickListener { onClickItem(data.partyId) }
            btnAction.setOnClickListener { onClickAction(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyViewHolder {
        val binding = ItemPartyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PartyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PartyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    private fun formatDateTime(date: String, start: String, end: String): String {
        // date: "2025-12-31"
        val parts = date.split("-")
        val month = parts[1].toInt()
        val day = parts[2].toInt()

        return "${month}월 ${day}일 ${start} ~ ${end}"
    }

}
