package com.codingnight.example.gallery

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout

class GalleryAdapter : ListAdapter<PhotoItem, RecyclerView.ViewHolder>(DIFFCALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder: RecyclerView.ViewHolder
        if (viewType == NORMAL_VIEW_TYPE) {
            holder = ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_cell, parent, false)
            )
            holder.itemView.setOnClickListener {
                Bundle().apply {
                    putParcelableArrayList("PHOTO_LIST", ArrayList(currentList))
                    putInt("PHOTO_POSITION", holder.adapterPosition)
                    holder.itemView.findNavController()
                        .navigate(R.id.action_galleryFragment_to_pagerPhotoFragment, this)
                }
            }
        } else {
            holder = FooterViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_footer, parent, false).also {
                    (it.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
                }
            )
        }
        return holder
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1)
            FOOTER_VIEW_TYPE
        else
            NORMAL_VIEW_TYPE
    }

    companion object {
        const val NORMAL_VIEW_TYPE = 0
        const val FOOTER_VIEW_TYPE = 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == itemCount - 1) {
            return
        }
        val photoItem = getItem(position)
        holder as ItemViewHolder
        with(holder) {
            imageView.layoutParams.height = photoItem.photoHeight
            textViewUser.text = photoItem.photoUser
            textViewCollections.text = photoItem.photoCollections.toString()
            textViewLikes.text = photoItem.photoLikes.toString()
        }
        val shimmerBuilder = Shimmer.ColorHighlightBuilder()
        holder.shimmerViewCell.apply {
            setShimmer(
                shimmerBuilder
                    .setHighlightColor(0x55FFFFFF)
                    .setBaseAlpha(1f)
                    .setBaseColor(0xD9D9D9)
                    .setDropoff(0.3f)
                    .setTilt(0f)
                    .build()
            )
            startShimmer()
        }

        Glide.with(holder.itemView)
            .load(photoItem.fullUrl)
            .placeholder(R.drawable.photo_placeholder)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false.also {
                        holder.shimmerViewCell.setShimmer(null)
                    }
                }

            })
            .into(holder.imageView)
    }

    object DIFFCALLBACK : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            // DataClass 重载 == 运算符实现了逐项比较
            return oldItem == newItem
        }
    }
}


class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val shimmerViewCell: ShimmerFrameLayout = itemView.findViewById(R.id.shimmerViewCell)
    val imageView: ImageView = itemView.findViewById(R.id.imageView)
    val textViewUser: TextView = itemView.findViewById(R.id.textViewUser)
    val textViewLikes: TextView = itemView.findViewById(R.id.textViewLikes)
    val textViewCollections: TextView = itemView.findViewById(R.id.textViewCollections)
}

class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
}