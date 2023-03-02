package com.memories.memories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.memories.memories.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {

private lateinit var binding: FragmentRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(requireActivity(),R.layout.fragment_register)
        Glide.with(this@RegisterFragment).asGif().load(R.drawable.third_screen_gif).into(binding.loginanim);
        binding.loginbutton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }



}