package com.memories.memories

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.memories.memories.databinding.ActivityMainBinding.bind
import com.memories.memories.databinding.ActivityMainBinding.inflate
import com.memories.memories.databinding.FragmentLoginBinding
import com.utils.showToast


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = DataBindingUtil.setContentView(requireActivity(),R.layout.fragment_login)
        Glide.with(this@LoginFragment).asGif().load(R.drawable.second_screen_gif).into(binding.loginanim);
        binding.createone.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)

        }
        binding.btnlogin.setOnClickListener{
            login()
        }
        binding.forgotpassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
        }


    }
    private fun login() {
        binding. email.text.toString()
        binding.password.text.toString()
        // calling signInWithEmailAndPassword(email, pass)
        // function using Firebase auth object
        // On successful response Display a Toast

        if (binding.email.text.isBlank() || binding.password.text.isBlank()) {
            Toast.makeText(requireContext(), "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword( binding. email.text.toString(), binding.password.text.toString())
            .addOnCompleteListener(requireActivity()) {
            if (it.isSuccessful) {
                (activity as MainActivity).showToast("Login Successfull")
                findNavController().navigate(R.id.action_loginFragment_to_allDatesFragment2)
            } else
                (activity as MainActivity).showToast("Please Enter valid Credentials")
        }.addOnFailureListener {

            }
    }

}