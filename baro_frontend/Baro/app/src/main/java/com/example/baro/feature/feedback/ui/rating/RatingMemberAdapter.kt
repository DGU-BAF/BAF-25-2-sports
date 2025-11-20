// feature/feedback/ui/rating/RatingMemberAdapter.kt
package com.example.baro.feature.feedback.ui.rating

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.example.baro.databinding.ItemRatingMemberBinding

class RatingMemberAdapter(
    private val onRatingChanged: (userId: String, rating: Int) -> Unit
) : RecyclerView.Adapter<RatingMemberAdapter.VH>() {

    private val items = mutableListOf<RatingMemberUiModel>()

    fun submitList(list: List<RatingMemberUiModel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(
        private val binding: ItemRatingMemberBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RatingMemberUiModel) = with(binding) {
            tvNickname.text = item.nickname
            // 리스너 제거 후 값 세팅
            rbMemberRating.setOnRatingBarChangeListener(null)
            rbMemberRating.rating = item.rating.toFloat()
            rbMemberRating.setOnRatingBarChangeListener { _: RatingBar, rating, _ ->
                onRatingChanged(item.userId, rating.toInt())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemRatingMemberBinding.inflate(
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
