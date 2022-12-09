package com.example.contacts.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.contacts.databinding.FragmentNewContactBinding
import com.example.contacts.domain.Contact
import com.example.contacts.other.Constants.IMAGE_REQUEST_KEY
import com.example.contacts.other.Constants.URI_BUNDLE_KEY
import com.example.contacts.ui.fragments.dialogs.PhotoPickerDialogFragment
import com.example.contacts.util.insertContact
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class NewContactFragment : Fragment() {
    private lateinit var binding: FragmentNewContactBinding
    private lateinit var imageUri: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewContactBinding.inflate(inflater)
        setClickListeners()

        setFragmentResultListener(IMAGE_REQUEST_KEY) { requestKey, bundle ->
            Toast.makeText(
                requireContext(),
                "Fragment Result callback executed",
                Toast.LENGTH_SHORT
            ).show()
            bundle.getString(URI_BUNDLE_KEY)?.let { imageUri = it }
            if (::imageUri.isInitialized) {
                binding.profileImage.setImageURI(Uri.parse(imageUri))
                Toast.makeText(requireContext(), "Image Uri: $imageUri", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    private fun setClickListeners() {
        binding.apply {
            cancelButton.setOnClickListener { findNavController().navigateUp() }

            saveButton.setOnClickListener {
                if (validateInputs()) { addContact() }
            }

            profileImage.setOnClickListener {
                PhotoPickerDialogFragment().show(
                    parentFragmentManager, PhotoPickerDialogFragment.TAG
                )
            }

            dateEt.setOnClickListener { showDatePickerDialog() }

            // textInputEditText validations
            firstNameEt.doOnTextChanged { text, _, _, _ ->
                firstNameTil.helperText = if ("$text".isEmpty()) "Required*" else null
                firstNameTil.error = if ("$text".isBlank()) "Invalid Name" else null
            }

            emailEt.doOnTextChanged { text, _, _, _ ->
                emailTil.error = validEmail("$text")
            }

            phone1Et.doOnTextChanged { text, _, _, _ ->
                phone1Til.error = validPhone("$text")
                if ("$text".isNotBlank()) phone1Til.helperText = null
            }
        }
    }

    private fun validateInputs(): Boolean {
        val nameNotBlank = "${binding.firstNameEt.text}".isNotBlank()
        val phoneNotBlank = "${binding.phone1Et.text}".isNotBlank()
        val phoneLengthValid = "${binding.phone1Et.text}".length == 10

        if (! nameNotBlank) { binding.firstNameTil.helperText = "Required*" }
        if (! phoneNotBlank) { binding.phone1Til.helperText = "Required*" }

        return nameNotBlank && phoneNotBlank && phoneLengthValid
    }

    private fun validName(name: String): String? {
        return if (name.isEmpty()) "Required*" else null
    }

    private fun validEmail(email: String): String? {
        return if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Invalid Email Address" else null
    }

    private fun validPhone(phone: String): String? {
        return if (phone.length != 10) "Must be 10 Digits" else null
    }

    private fun showDatePickerDialog() {
        val materialDatePicker =
            MaterialDatePicker.Builder.datePicker().setTitleText("Select Date").build()

        materialDatePicker.addOnPositiveButtonClickListener {
            it?.let {
                val date = DateFormat.format("dd/MM/yyyy", Date(it)).toString()
                binding.dateEt.setText(date)
            }
        }

        materialDatePicker.show(childFragmentManager, "TAG")
    }

    private fun addContact() {
        val firstName = binding.firstNameEt.text.toString().trim()
        val lastName = binding.lastNameEt.text.toString().trim()
        val phoneNumber = binding.phone1Et.text.toString().trim()
        val email = binding.emailEt.text.toString().trim()

        val newContact = Contact(firstName, lastName, imageUri, phoneNumber, email)

        insertContact(requireContext(), newContact)

        Toast.makeText(requireContext(), "Contact saved", Toast.LENGTH_LONG).show()
    }
}