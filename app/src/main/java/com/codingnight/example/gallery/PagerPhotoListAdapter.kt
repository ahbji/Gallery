package com.codingnight.example.gallery

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout

class PagerPhotoListAdapter : ListAdapter<PhotoItem, PagerPhotoViewHolder>(DiffCallback) {
    object DiffCallback : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerPhotoViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.pager_photo_view,parent, false).apply {
            return PagerPhotoViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: PagerPhotoViewHolder, position: Int) {
        val photoItem = getItem(position)
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
}

class PagerPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val shimmerViewCell: ShimmerFrameLayout = itemView.findViewById(R.id.shimmerLayoutPhoto)
    val imageView: ImageView = itemView.findViewById(R.id.photoView)
}
