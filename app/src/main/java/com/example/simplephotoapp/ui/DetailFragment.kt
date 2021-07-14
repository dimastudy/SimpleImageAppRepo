package com.example.simplephotoapp.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.simplephotoapp.R
import com.example.simplephotoapp.databinding.FragmentDetailPhotoBinding
import com.example.simplephotoapp.viewmodels.DetailPhotoViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail_photo) {


    private var _binding: FragmentDetailPhotoBinding? = null
    private val binding get() = _binding
    private var isFavorite: Boolean = false


    @Inject lateinit var factory: DetailPhotoViewModel.Factory.Factory

    private val viewModel: DetailPhotoViewModel by viewModels {
        val photo = DetailFragmentArgs.fromBundle(requireArguments()).photo
        factory.create(photo)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentDetailPhotoBinding.bind(view)
        viewModel.photoProperty.observe(viewLifecycleOwner) { photo ->
            binding?.apply {
                pbImageLoading.isVisible = true
                Glide.with(this@DetailFragment)
                    .load(photo.photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            pbImageLoading.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            pbImageLoading.isVisible = false
                            tvDescription.isVisible = photo.imageDescription != null
                            return false
                        }

                    })
                    .into(imagePhotoDetail)
                tvNickname.text = photo.creatorNickname
                tvDescription.text = photo.imageDescription
                tvLikes.text = "${photo.photoLikes} likes"
            }

        }

        viewModel.isFavorite.observe(viewLifecycleOwner) {
            isFavorite = it == true
            requireActivity().invalidateOptionsMenu()
        }

        binding!!.btnDownloadImage.setOnClickListener {
            val imageUrl = viewModel.photoProperty.value?.photoUrl
            viewModel.downloadImage(imageUrl!!, requireActivity())
        }

        viewModel.downloadPhoto.observe(viewLifecycleOwner) {
            if (it == true) {
                Snackbar.make(view, "Downloaded!", Snackbar.LENGTH_LONG).show()
            }
        }


        (activity as AppCompatActivity).supportActionBar?.title = "Image Details"
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return super.onOptionsItemSelected(item)
        val photo = DetailFragmentArgs.fromBundle(requireArguments()).photo
        return when (item.itemId) {
            R.id.action_favorite -> {

                if (isFavorite) {
                    viewModel.deleteFavoritePhoto(photo)
                    item.icon = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_favorite_border_24
                    )
                    Snackbar.make(requireView(), "Deleted from Favorites", Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    viewModel.addPhotoToFavorite(photo)
                    item.icon = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_favorite_24
                    )
                    Snackbar.make(requireView(), "Added to Favorites", Snackbar.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_favorite)
        if (isFavorite) {
            item.icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_favorite_24)
        } else {
            item.icon = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_favorite_border_24
            )
        }
    }

}