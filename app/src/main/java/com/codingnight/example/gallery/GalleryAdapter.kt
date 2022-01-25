package com.codingnight.example.gallery

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout

class GalleryAdapter(private val viewModel: GalleryViewModel) : PagedListAdapter<PhotoItem, RecyclerView.ViewHolder>(DIFFCALLBACK) {

    private var networkStatus: NetworkStatus? = null

    private var hasFooter = false

    init {
        viewModel.retry()
    }

    fun updateNetworkStatus(networkStatus: NetworkStatus?) {
        this.networkStatus = networkStatus
        if (networkStatus == NetworkStatus.INITIAL_LOADING) hideFooter() else showFooter()
    }

    private fun hideFooter() {
        if (hasFooter) {
            notifyItemRemoved(itemCount - 1)
        }
        hasFooter = false
    }

    private fun showFooter() {
        if (hasFooter) {
            notifyItemChanged(itemCount - 1)
        } else {
            hasFooter = true
            notifyItemInserted( itemCount - 1)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasFooter) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasFooter && position == itemCount - 1)
            R.layout.gallery_footer
        else
            R.layout.gallery_cell
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.gallery_cell -> PhotoViewHolder.newInstance(parent).also { holder ->
                holder.itemView.setOnClickListener {
                    Bundle().apply {
                        putInt("PHOTO_POSITION", holder.adapterPosition)
                        holder.itemView.findNavController()
                            .navigate(R.id.action_galleryFragment_to_pagerPhotoFragment, this)
                    }
                }
            }
            else -> FooterViewHolder.newInstance(parent).also { holder ->
                holder.itemView.setOnClickListener {
                    viewModel.retry()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            R.layout.gallery_footer -> (holder as FooterViewHolder).bindWithNetworkState(networkStatus)
            else -> {
                val photoItem = getItem(position) ?: return
                (holder as PhotoViewHolder).bindWithPhotoItem(photoItem)
            }
        }
    }

    object DIFFCALLBACK : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }
    }
}

class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val shimmerViewCell: ShimmerFrameLayout = itemView.findViewById(R.id.shimmerViewCell)
    private val imageView: ImageView = itemView.findViewById(R.id.imageView)
    private val textViewUser: TextView = itemView.findViewById(R.id.textViewUser)
    private val textViewLikes: TextView = itemView.findViewById(R.id.textViewLikes)
    private val textViewCollections: TextView = itemView.findViewById(R.id.textViewCollections)

    companion object {
        fun newInstance(parent: ViewGroup): PhotoViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_cell, parent, false)
            return PhotoViewHolder(view)
        }
    }

    fun bindWithPhotoItem(photoItem: PhotoItem) {
        imageView.layoutParams.height = photoItem.photoHeight
        textViewUser.text = photoItem.photoUser
        textViewCollections.text = photoItem.photoCollections.toString()
        textViewLikes.text = photoItem.photoLikes.toString()

        val shimmerBuilder = Shimmer.ColorHighlightBuilder()
        shimmerViewCell.apply {
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

        Glide.with(itemView)
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
                        shimmerViewCell.setShimmer(null)
                    }
                }

            })
            .into(imageView)
    }
}

class FooterViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {
    private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    private val textView: TextView = itemView.findViewById(R.id.textView)

    companion object {
        fun newInstance(parent: ViewGroup): FooterViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_footer, parent, false)
            (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
            return FooterViewHolder(view, parent.context)
        }
    }

    fun bindWithNetworkState(networkStatus: NetworkStatus?) {
        with(itemView) {
            when(networkStatus) {
                NetworkStatus.FAILED -> {
                    textView.text = context.getString(R.string.net_error_with_click)
                    progressBar.visibility = View.GONE
                    itemView.isClickable = true
                }
                NetworkStatus.COMPLETED -> {
                    textView.text = context.getString(R.string.load_complete)
                    progressBar.visibility = View.GONE
                    itemView.isClickable = false
                }
                else -> {
                    textView.text = context.getString(R.string.loading)
                    progressBar.visibility = View.VISIBLE
                    itemView.isClickable = false
                }
            }
        }
    }
}