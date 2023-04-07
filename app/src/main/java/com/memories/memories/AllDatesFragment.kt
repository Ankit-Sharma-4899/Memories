package com.memories.memories

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.memories.memories.Adapters.DatesAdapter
import com.memories.memories.databinding.FragmentAllDatesBinding
import java.util.*


class AllDatesFragment : Fragment() {

private lateinit var binding: FragmentAllDatesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(requireActivity(),R.layout.fragment_all_dates)
        binding.confirm.setOnClickListener {
            findNavController().navigate(R.id.action_allDatesFragment2_to_fragmentDisplayImages)
        }
        binding.calendarDate.setOnClickListener{
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    // Update the selected date in a TextView or any other view
                    val selectedDate = "$dayOfMonth/${month + 1}/$year"
                    binding.date.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()
        }

    }
}