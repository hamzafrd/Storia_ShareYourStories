package com.hamzafrd.storia.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hamzafrd.storia.R
import com.hamzafrd.storia.data.local.entity.StoryEntity
import com.hamzafrd.storia.databinding.ItemStoryBinding
import com.hamzafrd.storia.utils.DateFormatter

class StoryPagingAdapter(private val onClick: (StoryEntity) -> Unit) :
    PagingDataAdapter<StoryEntity, StoryPagingAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stories = getItem(position)
        if (stories != null) {
            holder.bind(stories)

            holder.binding.root.setOnClickListener {
                onClick(stories)
            }
        }

    }

    inner class MyViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(story: StoryEntity) {
            binding.apply {
                tvItemName.text = story.name
                tvItemPublishedDate.text = DateFormatter.formatDate(story.createdAt)
                tvItemDescription.text = story.description
                imgPoster.loadImage(story.photoUrl)
            }
        }
    }

    fun ImageView.loadImage(photoUrl: String) {
        Glide.with(this)
            .load(photoUrl)
            .apply(
                RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error)
            )
            .into(this)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<StoryEntity> =
            object : DiffUtil.ItemCallback<StoryEntity>() {
                override fun areItemsTheSame(
                    oldItem: StoryEntity,
                    newItem: StoryEntity,
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: StoryEntity,
                    newItem: StoryEntity,
                ): Boolean {
                    return oldItem.id == newItem.id
                }
            }
    }
}