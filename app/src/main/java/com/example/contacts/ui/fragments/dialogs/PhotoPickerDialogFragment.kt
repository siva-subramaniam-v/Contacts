package com.example.contacts.ui.fragments.dialogs

import android.app.Activity
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.contacts.R
import com.example.contacts.databinding.FragmentPhotoPickerDialogBinding

class PhotoPickerDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentPhotoPickerDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentPhotoPickerDialogBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext()).setView(binding.root).create()

        dialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(requireActivity(), R.drawable.photo_picker_dialog_background)
        )

        binding.cancelText.setOnClickListener {
            dialog.dismiss()
        }

        setClickListeners()
        return dialog
    }

    private fun setClickListeners() {

        val takePicturePreviewLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) {
            it?.let {
                Toast.makeText(requireContext(), "Bmp hashcode: ${it.hashCode()}", Toast.LENGTH_SHORT).show()
            }
        }

        val getContentLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show()
        }

        binding.apply {
            takePhotoText.setOnClickListener {
                takePicturePreviewLauncher.launch()
            }

            choosePhotoText.setOnClickListener {
                getContentLauncher.launch("image/*")
            }
        }
    }

    companion object {
        const val TAG = "PhotoPickerDialog"
    }
}
