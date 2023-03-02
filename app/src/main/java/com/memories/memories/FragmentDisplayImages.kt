package com.memories.memories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.memories.memories.databinding.FragmentDisplayImagesBinding


class FragmentDisplayImages : Fragment() {

private lateinit var binding: FragmentDisplayImagesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding=DataBindingUtil.setContentView(requireActivity(),R.layout.fragment_display_images)

    return view
    }


}