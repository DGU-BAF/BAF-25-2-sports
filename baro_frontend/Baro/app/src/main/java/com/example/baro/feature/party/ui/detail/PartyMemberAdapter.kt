package com.example.baro.feature.party.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.baro.databinding.ItemMemberBinding
import com.example.baro.feature.party.domain.model.PartyMember
import com.example.baro.feature.party.domain.model.PartyMemberRole

class PartyMemberAdapter :
    RecyclerView.Adapter<PartyMemberAdapter.MemberViewHolder>() {

    private val items = mutableListOf<PartyMember>()

    fun submitList(list: List<PartyMember>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class MemberViewHolder(
        private val binding: ItemMemberBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PartyMember) = with(binding) {
            val displayName = if (item.role == PartyMemberRole.HOST) {
                "${item.nickname}(방장)"
            } else {
                item.nickname
            }

            nickname.text = displayName
            sportsmanship.text = item.sportsmanship?.toString() ?: "-"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMemberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
