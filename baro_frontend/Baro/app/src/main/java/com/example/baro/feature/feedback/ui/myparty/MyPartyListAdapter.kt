// feature/feedback/ui/myparty/MyPartyListAdapter.kt
package com.example.baro.feature.feedback.ui.myparty

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.baro.databinding.ItemMypartyBinding

class MyPartyListAdapter(
    private val onClickItem: (MyPartyListItemUiModel) -> Unit
) : RecyclerView.Adapter<MyPartyListAdapter.VH>() {

    private val items = mutableListOf<MyPartyListItemUiModel>()

    fun submitList(list: List<MyPartyListItemUiModel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(
        private val binding: ItemMypartyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MyPartyListItemUiModel) = with(binding) {
            tvTitle.text = item.title
            tvDate.text = item.displayDate
            tvStatus.text = item.statusText

            root.isEnabled = item.canClickForFeedback
            root.alpha = if (item.canClickForFeedback) 1f else 0.4f

            root.setOnClickListener {
                if (item.canClickForFeedback) {
                    onClickItem(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMypartyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
