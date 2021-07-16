package com.example.simplephotoapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.simplephotoapp.R
import com.example.simplephotoapp.databinding.FragmentWelcomeScreenBinding
import com.example.simplephotoapp.viewmodels.WelcomeViewModel

class WelcomeScreenFragment : Fragment(R.layout.fragment_welcome_screen) {

    var _binding: FragmentWelcomeScreenBinding? = null
    val binding: FragmentWelcomeScreenBinding get() = _binding!!
    val viewModel: WelcomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentWelcomeScreenBinding.bind(view)

        val animSimple = AnimationUtils.loadAnimation(requireContext(), R.anim.welcome_simple_anim)
        val animImage = AnimationUtils.loadAnimation(requireContext(), R.anim.welcome_image_anim)
        val animApp = AnimationUtils.loadAnimation(requireContext(), R.anim.welcome_app_anim)
        val animLayout = AnimationUtils.loadAnimation(requireContext(), R.anim.to_up_slide)

        binding.apply {
            welcomeContainer.startAnimation(animLayout)
            imageWelcomeS.startAnimation(animSimple)
            imageWelcomeA.startAnimation(animApp)
            imageWelcomeI.startAnimation(animImage)
        }
        viewModel.navigateToPhotosScreen()

        viewModel.navigateToPhotos.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController().navigate(
                    WelcomeScreenFragmentDirections
                        .actionWelcomeScreenFragmentToPhotosFragment()
                )
                viewModel.doneNavigating()
            }
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}