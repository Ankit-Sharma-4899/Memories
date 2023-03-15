package com.memories.memories

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.memories.memories.databinding.FragmentLoginBinding
import com.memories.memories.databinding.FragmentSplashBinding
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(requireActivity(),R.layout.fragment_splash)
        binding.btnGetstarted.setOnClickListener{
            findNavController().navigate(R.id.action_nav_splash_to_loginFragment)
        }
        navigation()
    }

    private fun navigation() {
        val shake: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.my_anim)
        binding.logocard.animation = shake
        binding.textget.animation=shake
        Handler(Looper.getMainLooper()).postDelayed({
            binding.btnGetstarted.visibility=View.VISIBLE
        }, 2000)

    }
}