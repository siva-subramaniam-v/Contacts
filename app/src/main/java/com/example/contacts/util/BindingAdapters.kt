package com.example.contacts.util

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.example.contacts.R

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUri: String) {
    if (imgUri.isNotBlank()) {
        Log.i("BindingAdapter", imgUri)

        val uri = Uri.parse(imgUri)
        imgView.setImageURI(uri)
    }
}