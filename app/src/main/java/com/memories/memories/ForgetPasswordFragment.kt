package com.memories.memories

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.memories.memories.databinding.FragmentForgetPasswordBinding
import com.utils.showToast


class ForgetPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgetPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = DataBindingUtil.setContentView(requireActivity(),R.layout.fragment_forget_password)
        //val username = view.findViewById<EditText>(R.id.editresetpassword)
        binding.submitreset.setOnClickListener {
            forgotPassword(binding.editresetpassword.text.toString())
        }
    }
    private fun forgotPassword (username: String){
        if (binding.editresetpassword.text.toString().isEmpty()){
            (activity as MainActivity).showToast("Please Enter Email Id")
        }
        auth.sendPasswordResetEmail(binding.editresetpassword.text.toString())
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    binding.submitreset.setBackgroundColor(Color.LTGRAY)
                    (activity as MainActivity).showToast("Email Sent")
                }
            }
    }


}