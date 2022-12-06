package com.example.contacts.ui.fragments

import android.Manifest
import android.content.ContentProviderOperation
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.RawContacts
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Data
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.contacts.BuildConfig
import com.example.contacts.databinding.FragmentNewContactBinding
import com.example.contacts.ui.fragments.dialogs.PhotoPickerDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.util.*

class NewContactFragment : Fragment() {
    private lateinit var binding: FragmentNewContactBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewContactBinding.inflate(inflater)

        binding.dateEt.setOnClickListener {
            showDatePickerDialog()
        }

        binding.saveButton.setOnClickListener {
            onClickRequestPermission()
            //Toast.makeText(requireContext(), "Contact saved", Toast.LENGTH_SHORT).show()
        }

        binding.profileImage.setOnClickListener{
            PhotoPickerDialogFragment().show(
                childFragmentManager, PhotoPickerDialogFragment.TAG
            )
        }

        return binding.root
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
        val contentProviderOperations = arrayListOf<ContentProviderOperation>()

        contentProviderOperations.add(
            ContentProviderOperation.newInsert(
                RawContacts.CONTENT_URI
            )
                .withValue(RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null)
                .build()
        )

        // Adding name
        contentProviderOperations.add( ContentProviderOperation
            .newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
            .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
            .withValue(StructuredName.DISPLAY_NAME, "${binding.firstNameEt.text} ${binding.lastNameEt.text}")
            .build()
        )

        // Adding Number
        contentProviderOperations.add( ContentProviderOperation
            .newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
            .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
            .withValue(Phone.NUMBER, "${binding.phone1Et.text}")
            .withValue(Phone.TYPE, Phone.TYPE_WORK)
            .build()
        )

        try {
            requireActivity().contentResolver.applyBatch(ContactsContract.AUTHORITY, contentProviderOperations)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // Permission handling
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            addContact()
            Log.i("Permission: ", "Granted")
        } else {
            Snackbar.make(
                binding.root,
                "Contacts permission is required to save contacts to your phone",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("OK") {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                intent.data = uri
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }.show()
            Log.i("Permission: ", "Denied")
        }
    }

    private fun onClickRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                addContact()
                Toast.makeText(requireContext(), "Contact saved successfully", Toast.LENGTH_SHORT).show()
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.WRITE_CONTACTS
                )
            }
        }
    }
}