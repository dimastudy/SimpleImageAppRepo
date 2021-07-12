package com.example.simplephotoapp.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.simplephotoapp.data.domain.PhotoDomain
import com.example.simplephotoapp.databinding.PhotoItemBinding

class FavoritePhotosAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<PhotoDomain, FavoritePhotosAdapter.FavoritePhotosViewHolder>(DiffCallback) {
    class FavoritePhotosViewHolder(private val binding: PhotoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photoDomain: PhotoDomain) {
            binding.apply {
                Glide.with(itemView)
                    .load(photoDomain.photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            tvCreator.isVisible = false
                            pbImageLoad.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            pbImageLoad.isVisible = false
                            tvCreator.isVisible = true
                            return false
                        }

                    })
                    .into(imageUnsplashPhoto)

                tvCreator.text = photoDomain.creatorNickname
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritePhotosViewHolder {

        val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritePhotosViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FavoritePhotosViewHolder,
        position: Int
    ) {

        val photo = getItem(position)!!
        holder.itemView.setOnClickListener {
            onClickListener.click(photo)
        }
        holder.bind(photo)
    }

    object DiffCallback : DiffUtil.ItemCallback<PhotoDomain>() {
        override fun areItemsTheSame(oldItem: PhotoDomain, newItem: PhotoDomain): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: PhotoDomain, newItem: PhotoDomain): Boolean =
            oldItem == newItem

    }

    class OnClickListener(val clickListener: (photo: PhotoDomain) -> Unit) {
        fun click(photoDomain: PhotoDomain) = clickListener(photoDomain)
    }

}