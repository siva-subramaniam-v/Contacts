package com.example.contacts.ui.fragments

import android.os.Bundle
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.contacts.databinding.FragmentNewContactBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class NewContactFragment : Fragment() {
    private lateinit var binding: FragmentNewContactBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewContactBinding.inflate(inflater)

        binding.dateEt.setOnClickListener {
            showDatePickerDialog()
        }

        binding.saveButton.setOnClickListener {
            Toast.makeText(requireContext(), "Contact saved", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun showDatePickerDialog() {
        val materialDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .build()

        materialDatePicker.addOnPositiveButtonClickListener {
            it?.let {
                val date = DateFormat.format("dd/MM/yyyy", Date(it)).toString()
                binding.dateEt.setText(date)
            }
        }

        materialDatePicker.show(childFragmentManager, "TAG")
    }
}