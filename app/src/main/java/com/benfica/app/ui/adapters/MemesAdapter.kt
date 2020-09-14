package com.benfica.app.ui.adapters

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.benfica.app.R
import com.benfica.app.data.models.Meme
import com.benfica.app.data.wrappers.ItemViewModel
import com.benfica.app.data.wrappers.ObservableMeme
import com.benfica.app.databinding.ItemMemeBinding
import com.benfica.app.ui.callbacks.MemesCallback
import com.benfica.app.utils.AppUtils
import com.benfica.app.utils.TimeFormatter
import com.benfica.app.utils.inflate
import com.benfica.app.utils.setDrawable
import com.mikepenz.ionicons_typeface_library.Ionicons
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber

class MemesAdapter(private val callback: MemesCallback): PagedListAdapter<ItemViewModel, MemesAdapter.MemeHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemViewModel>() {
            override fun areItemsTheSame(oldItem: ItemViewModel, newItem: ItemViewModel): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: ItemViewModel, newItem: ItemViewModel): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeHolder {
        return MemeHolder(parent.inflate(R.layout.item_meme), callback)
    }

    override fun onBindViewHolder(holder: MemeHolder, position: Int) {
        val meme = getItem(position) as ObservableMeme

        meme.meme.subscribeBy(
                onNext = { holder.bind(it) },
                onError = {
                    Timber.e("Meme deleted")
                    this.currentList?.dataSource?.invalidate()
                }
        ).addTo(holder.disposables)
    }

    override fun onViewRecycled(holder: MemeHolder) {
        super.onViewRecycled(holder)
        holder.apply { disposables.clear() }
    }

    inner class MemeHolder(private val binding: ItemMemeBinding, private val callback: MemesCallback):
            RecyclerView.ViewHolder(binding.root) {
        val disposables = CompositeDisposable()

        init {
            binding.memeMore.apply {
                setImageDrawable(AppUtils.getDrawable(this.context, Ionicons.Icon.ion_android_more_vertical, R.color.color_text_secondary, 14))
            }

            binding.memeComment.apply {
                this.setDrawable(AppUtils.getDrawable(this.context, Ionicons.Icon.ion_ios_chatboxes_outline, R.color.color_text_secondary, 20))
            }
        }

        // Bind meme object to layout
        fun bind(meme: Meme) {
            Timber.e("Binding ${meme.id}")

            if (binding.meme != null && binding.meme!!.id == meme.id) {
                meme.imageUrl = null
            }
            println("hggggggggghgghghhg"+meme.imageUrl)
            binding.meme = meme
            binding.callback = callback
            binding.timeFormatter = TimeFormatter()
        }
    }

}