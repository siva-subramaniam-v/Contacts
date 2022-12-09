package com.example.contacts.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.contacts.BuildConfig
import com.example.contacts.R
import com.example.contacts.adapters.ContactsAdapter
import com.example.contacts.databinding.FragmentDisplayContactsBinding
import com.example.contacts.util.getCursor
import com.google.android.material.snackbar.Snackbar

class DisplayContactsFragment : Fragment() {
    private lateinit var binding: FragmentDisplayContactsBinding
    private lateinit var adapter: ContactsAdapter
    private lateinit var cursor: Cursor

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

        binding.search.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { getContacts() }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { getContacts() }
                    return true
                }

            }
        )

        return binding.root
    }

    private fun getContacts() {
        getCursor(requireContext())?.let { cursor = it }

        if (::cursor.isInitialized) {
            adapter = ContactsAdapter(cursor)
            binding.contactsList.adapter = adapter
        }
    }

    // Permission handling
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            Log.i("Permission: ", "Granted")
//            getContacts()
//        } else {
//            Snackbar.make(
//                binding.root,
//                "Contacts permission is required to fetch contacts from your phone",
//                Snackbar.LENGTH_INDEFINITE
//            ).setAction("OK") {
//                val intent = Intent()
//                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
//                intent.data = uri
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//            }.show()
//            Log.i("Permission: ", "Denied")
//        }
//    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getContacts()
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_CONTACTS
                    )
                ) {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.enable_permission),
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(R.string.ok) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        intent.data = uri
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }.show()
                }
            }
        }

    private fun onClickRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                getContacts()
                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_CONTACTS
            ) -> {
                Snackbar.make(
                    binding.root,
                    getString(R.string.permission_required),
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.ok) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.READ_CONTACTS
                    )
                }.show()
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_CONTACTS
                )
            }
        }
    }


    override fun onResume() {
        super.onResume()
        refreshCursor()
    }

    override fun onDestroy() {
        super.onDestroy()
        closeCursor()
    }

    private fun refreshCursor() {
        closeCursor()

        if (::adapter.isInitialized) {
            getCursor(requireContext())?.let {
                cursor = it
                adapter.submitCursor(it)
            }
        }
    }

    private fun closeCursor() {
        if (::cursor.isInitialized)
            cursor.close()
    }
}