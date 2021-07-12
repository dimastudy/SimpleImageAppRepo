package com.example.simplephotoapp.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.simplephotoapp.R
import com.example.simplephotoapp.adapters.FavoritePhotosAdapter
import com.example.simplephotoapp.databinding.FragmentFavoritePhotosBinding
import com.example.simplephotoapp.viewmodels.FavoritePhotosViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavoritePhotosFragment : Fragment(R.layout.fragment_favorite_photos) {


    private val viewModel by viewModels<FavoritePhotosViewModel>()
    private var _binding: FragmentFavoritePhotosBinding? = null
    private val binding get() = _binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FavoritePhotosAdapter(FavoritePhotosAdapter.OnClickListener { photo ->
            viewModel.navigateToDetailPhoto(photo)
        })

        _binding = FragmentFavoritePhotosBinding.bind(view)

        binding?.apply {
            rvFavoritePhotos.adapter = adapter
            rvFavoritePhotos.setHasFixedSize(true)
        }

        viewModel.favoritePhotos.observe(viewLifecycleOwner) { photos ->
            adapter.submitList(photos)
        }

        viewModel.navigateToDetailPhoto.observe(viewLifecycleOwner){
            if(it != null){
                view.findNavController().navigate(FavoritePhotosFragmentDirections.actionFavoritePhotosFragmentToDetailFragment(it))

                viewModel.doneNavigating()
            }
        }


        (activity as AppCompatActivity).supportActionBar?.title = "Favorite images"


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}