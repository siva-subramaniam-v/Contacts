package com.example.contacts.domain

import android.graphics.Bitmap

data class Contact(
    val firstName: String,
    val lastName: String,
    val profileImgUrl: String?,
    val phoneNumber: String,
    val email: String
)
