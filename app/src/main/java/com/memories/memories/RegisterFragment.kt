package com.memories.memories

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.memories.memories.databinding.FragmentRegisterBinding
import com.utils.showToast


class RegisterFragment : Fragment() {

private lateinit var binding: FragmentRegisterBinding
private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        binding = DataBindingUtil.setContentView(requireActivity(),R.layout.fragment_register)
        binding.btnregister.setOnClickListener{
            signUpUser()

        }
        Glide.with(this@RegisterFragment).asGif().load(R.drawable.third_screen_gif).into(binding.loginanim);
        binding.loginbutton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
    private fun signUpUser() {
        binding.emailregister.text.toString()
        binding.passwordregister.text.toString()
        binding.confirmpass.text.toString()

        // check pass
        if (binding.emailregister.text.toString().isBlank() || binding.passwordregister.text.toString().isBlank() || binding.confirmpass.text.toString().isBlank()) {
            Toast.makeText(requireContext(), "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (binding.passwordregister.text.toString() != binding.confirmpass.text.toString()) {
            (activity as MainActivity).showToast("Password and confirm password do not match")
            return
        }
        // If all credential are correct
        // We call createUserWithEmailAndPassword
        // using auth object and pass the
        // email and pass in it.
        auth.createUserWithEmailAndPassword(binding.emailregister.text.toString(), binding.passwordregister.text.toString())
            .addOnCompleteListener(requireActivity()) {
            if (it.isSuccessful) {
                (activity as MainActivity).showToast("Login Successfully")
                findNavController().navigate(R.id.action_registerFragment_to_allDatesFragment2)

            } else {
                (activity as MainActivity).showToast("Signed in failed")
                Log.d("hghghb", "$it")
            }
        }.addOnFailureListener {
                (activity as MainActivity).showToast("Please Enter Strong Password")
            }
    }
}

