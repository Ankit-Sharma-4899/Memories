package com.memories.memories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.memories.memories.databinding.ActivityMainBinding.bind
import com.memories.memories.databinding.ActivityMainBinding.inflate
import com.memories.memories.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(requireActivity(),R.layout.fragment_login)
        Glide.with(this@LoginFragment).asGif().load(R.drawable.second_screen_gif).into(binding.loginanim);
        binding.createone.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)

        }
        binding.btnlogin.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_allDatesFragment2)
        }

    }

}