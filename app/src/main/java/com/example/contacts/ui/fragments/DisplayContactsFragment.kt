package com.example.contacts.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.contacts.BuildConfig
import com.example.contacts.databinding.FragmentDisplayContactsBinding
import com.google.android.material.snackbar.Snackbar

class DisplayContactsFragment: Fragment() {
    private lateinit var binding: FragmentDisplayContactsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDisplayContactsBinding.inflate(inflater)

        binding.fabAddContact.setOnClickListener {
            findNavController().navigate(DisplayContactsFragmentDirections.actionDisplayContactsFragmentToNewContactFragment())
        }

        binding.getContactsButton.setOnClickListener {
            onClickRequestPermission() // TODO: Get contact read and write permissions in this fragment
        }

        return binding.root
    }

    @SuppressLint("Range")
    private fun getContacts() {
        // TODO: Check permission to read contacts

        val contentResolver = requireContext().contentResolver

        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val cursor = contentResolver.query(uri, null, null, null, null)
        Log.i("Contact Provider Demo", "Contact count: ${cursor?.count}")

        cursor?.let {
            Log.i("Contact Provider Demo", "inside let block")
            while (it.moveToNext()) {
                val displayNameCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                val contactName = cursor.getString(displayNameCol)
                val contactNumber = cursor.getString(numberCol)

                Log.i("Contact Provider Demo", "Name: $contactName, Number: $contactNumber")
            }
        }

        cursor?.close()
    }


    // Permission handling
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.i("Permission: ", "Granted")
            getContacts()
        } else {
            Snackbar.make(
                binding.root,
                "Contacts permission is required to fetch contacts from your phone",
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
                requireContext(), Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                getContacts()
                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_CONTACTS
                )
            }
        }
    }
}