package com.example.simplephotoapp.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.simplephotoapp.data.models.Photo
import com.example.simplephotoapp.databinding.PhotoItemBinding

class PhotosListAdapter(private val onClickListener: OnClickListener):  PagingDataAdapter<Photo, PhotosListAdapter.PhotosViewHolder>(DiffCallback){

    class PhotosViewHolder(private val binding: PhotoItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(photo: Photo){
            binding.apply {
                Glide.with(itemView)
                    .load(photo.urls.regular)
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
                            tvCreator.isVisible = true
                            pbImageLoad.isVisible = false
                            return false
                        }

                    })
                    .into(imageUnsplashPhoto)

                tvCreator.text = photo.user.username
            }
        }
    }





    object DiffCallback: DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean =
            oldItem == newItem
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        val photo = getItem(position)!!
        holder.itemView.setOnClickListener {
            onClickListener.onClick(photo)
        }
        holder.bind(photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotosViewHolder(binding)
    }


    class OnClickListener(val clickListener: (photo: Photo) -> Unit){
        fun onClick(photo: Photo) = clickListener(photo)
    }

}


