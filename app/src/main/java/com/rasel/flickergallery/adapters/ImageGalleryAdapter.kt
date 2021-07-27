package com.rasel.flickergallery.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rasel.flickergallery.R
import com.rasel.flickergallery.data.models.Item
import com.rasel.flickergallery.databinding.LayoutImageCellBinding

class ImageGalleryAdapter : RecyclerView.Adapter<ImageGalleryAdapter.ImageGalleryViewHolder>() {

    var list: List<Item> = listOf()
    var onItemClick: ((Item) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGalleryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<LayoutImageCellBinding>(
            inflater, R.layout.layout_image_cell, parent, false
        )

        return ImageGalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageGalleryViewHolder, position: Int) {
        val photo = list[position]
        holder.bind(photo, onItemClick)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ImageGalleryViewHolder(private val binding: LayoutImageCellBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, onImageClick: ((Item) -> Unit)?) {
            binding.item = item
            binding.root.setOnClickListener {
                onImageClick?.invoke(item)
            }
            binding.executePendingBindings()
        }
    }
}