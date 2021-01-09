package com.panchayat.takoli.ui.adapters

import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.panchayat.takoli.R
import com.panchayat.takoli.data.models.Fave
import com.panchayat.takoli.databinding.ItemFaveBinding
import com.panchayat.takoli.ui.callbacks.FavesCallback
import com.panchayat.takoli.utils.TimeFormatter
import com.panchayat.takoli.utils.inflate
import timber.log.Timber

class FavesAdapter(private val callback: FavesCallback): PagedListAdapter<Fave, FavesAdapter.FaveHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Fave>() {
            override fun areItemsTheSame(oldItem: Fave, newItem: Fave): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Fave, newItem: Fave): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCurrentListChanged(previousList: PagedList<Fave>?, currentList: PagedList<Fave>?) {
        super.onCurrentListChanged(previousList, currentList)
        Timber.e("Previous list: $previousList vs Current list: $currentList")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaveHolder {
        return FaveHolder(parent.inflate(R.layout.item_fave), callback)
    }

    override fun onBindViewHolder(holder: FaveHolder, position: Int) {
        val fave = getItem(position)
        holder.bind(fave!!)
    }

    inner class FaveHolder(private val binding: ItemFaveBinding, private val callback: FavesCallback):
            RecyclerView.ViewHolder(binding.root) {

        fun bind(fave: Fave) {
            binding.meme = fave
            binding.callback = callback
            binding.timeFormatter = TimeFormatter()
        }

    }
}