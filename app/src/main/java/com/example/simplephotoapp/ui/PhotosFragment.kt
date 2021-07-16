package com.example.simplephotoapp.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.paging.LoadState
import com.example.simplephotoapp.R
import com.example.simplephotoapp.adapters.PhotosListAdapter
import com.example.simplephotoapp.adapters.UnsplashPhotoLoadStateAdapter
import com.example.simplephotoapp.databinding.FragmentPhotosListBinding
import com.example.simplephotoapp.viewmodels.PhotosViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PhotosFragment : Fragment(R.layout.fragment_photos_list) {

    private var _binding: FragmentPhotosListBinding? = null
    private val binding get() = _binding!!
    private lateinit var toggle: ActionBarDrawerToggle
    private val viewModel by viewModels<PhotosViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPhotosListBinding.bind(view)

        val drawerLayout = binding.drawerLayout
        val navView = binding.navDrawerView
        toggle = ActionBarDrawerToggle(activity, drawerLayout, R.string.open, R.string.close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val photosAdapter = PhotosListAdapter(PhotosListAdapter.OnClickListener { photo ->
            viewModel.navigateToDetailScreen(photo)
        })
        binding.apply {
            rvListPhotos.setHasFixedSize(true)
            rvListPhotos.adapter = photosAdapter.withLoadStateHeaderAndFooter(
                header = UnsplashPhotoLoadStateAdapter { photosAdapter.retry() },
                footer = UnsplashPhotoLoadStateAdapter { photosAdapter.retry() }
            )
            btnRetry.setOnClickListener { photosAdapter.retry() }
        }

        photosAdapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBarPhotos.isVisible = loadState.source.refresh is LoadState.Loading
                rvListPhotos.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                tvViewError.isVisible = loadState.source.refresh is LoadState.Error

                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    photosAdapter.itemCount < 1
                ) {
                    rvListPhotos.isVisible = false
                    tvViewEmpty.isVisible = true
                } else {
                    tvViewEmpty.isVisible = false
                }

            }
        }

        viewModel.photoData.observe(viewLifecycleOwner) { photosData ->
            photosAdapter.submitData(viewLifecycleOwner.lifecycle, photosData)
        }

        viewModel.detailNavigation.observe(viewLifecycleOwner) { photo ->
            if (photo != null) {
                view.findNavController()
                    .navigate(PhotosFragmentDirections.actionPhotosFragmentToDetailFragment(photo.getPhotoDomain()))

                viewModel.doneNavigatingToDetail()
            }
        }

        viewModel.favoritePhotosNavigation.observe(viewLifecycleOwner) {
            if (it) {
                view.findNavController()
                    .navigate(PhotosFragmentDirections.actionPhotosFragmentToFavoritePhotosFragment())

                viewModel.doneNavigatingToFavorite()
            }
        }

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_category_cars -> {
                    viewModel.searchForPhotos("cars")
                }
                R.id.action_category_cats -> {
                    viewModel.searchForPhotos("cats")
                }
                R.id.action_category_dogs -> {
                    viewModel.searchForPhotos("dogs")
                }
                R.id.action_category_nature -> {
                    viewModel.searchForPhotos("nature")
                }
                R.id.action_category_movies -> {
                    viewModel.searchForPhotos("movies")
                }
            }

            true
        }

        setHasOptionsMenu(true)

    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_photos, menu)

        val searchItem = menu.findItem(R.id.action_seacrh)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.rvListPhotos.scrollToPosition(0)
                    viewModel.searchForPhotos(query)
                    searchView.clearFocus()
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_to_favorite -> {
                viewModel.navigateToFavoriteScreen()
                return true
            }
        }
        if (toggle.onOptionsItemSelected(item)) {
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }
}