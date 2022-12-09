package com.example.contacts.ui.fragments.dialogs

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.example.contacts.BuildConfig
import com.example.contacts.R
import com.example.contacts.databinding.FragmentPhotoPickerDialogBinding
import com.example.contacts.other.Constants.IMAGE_REQUEST_KEY
import com.example.contacts.other.Constants.URI_BUNDLE_KEY
import com.example.contacts.other.TakePictureWithUriReturnContract
import java.io.File

class PhotoPickerDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentPhotoPickerDialogBinding

    private val takePictureLauncher =
        registerForActivityResult(TakePictureWithUriReturnContract()) { (isSuccess, imageUri) ->
            if (isSuccess) {
                setFragmentResult(IMAGE_REQUEST_KEY, bundleOf(URI_BUNDLE_KEY to "$imageUri"))
                dismiss()
            }
        }

    private val getContentLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
        imageUri?.let {
            setFragmentResult(IMAGE_REQUEST_KEY, bundleOf(URI_BUNDLE_KEY to "$imageUri"))
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentPhotoPickerDialogBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext()).setView(binding.root).create()

        dialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(requireActivity(), R.drawable.photo_picker_dialog_background)
        )

        binding.cancelText.setOnClickListener { dismiss() }

        setClickListeners()
        return dialog
    }

    private fun setClickListeners() {
        binding.apply {
            takePhotoText.setOnClickListener { takeImage() }
            choosePhotoText.setOnClickListener { selectImageFromGallery() }
        }
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri -> takePictureLauncher.launch(uri) }
        }
    }

    private fun selectImageFromGallery() = getContentLauncher.launch("image/jpeg")

    private fun getTmpFileUri(): Uri {
        val tmpFile =
            File.createTempFile("tmp_image_file", ".jpg", requireActivity().cacheDir).apply {
                createNewFile()
                deleteOnExit() // TODO: comment out this line
            }

        return FileProvider.getUriForFile(
            requireActivity().applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile
        )
    }

    companion object {
        const val TAG = "PhotoPickerDialog"
    }
}
