package com.hellcorp.gpstrackerpet.fragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hellcorp.gpstrackerpet.R
import com.hellcorp.gpstrackerpet.databinding.ItemTrackBinding
import com.hellcorp.gpstrackerpet.domain.TrackItem

class TrackAdapter : ListAdapter<TrackItem, TrackAdapter.TrackViewHolder>(Comparator) {
    class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemTrackBinding.bind(view)
        fun bind(track: TrackItem) = with(binding) {
            with(track) {
                tvDate.text = date
                tvTime.text = time
                tvDistance.text = distance
                tvSpeed.text = averageSpeed
            }
        }
    }

    object Comparator : DiffUtil.ItemCallback<TrackItem>() {
        override fun areItemsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
