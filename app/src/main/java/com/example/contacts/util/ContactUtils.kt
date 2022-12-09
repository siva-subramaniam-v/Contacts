package com.example.contacts.util

import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.RawContacts
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.AUTHORITY
import android.provider.ContactsContract.CommonDataKinds.Photo
import android.provider.MediaStore
import android.util.Log
import com.example.contacts.domain.Contact

private val fields = arrayOf(
    Phone._ID,
    Phone.DISPLAY_NAME,
    Phone.NUMBER,
    Phone.PHOTO_THUMBNAIL_URI
)

fun getCursor(context: Context) = context.contentResolver.query(
    Phone.CONTENT_URI,
    fields,
    null,
    null,
    Phone.DISPLAY_NAME
)

val Cursor.id: String
    @SuppressLint("Range")
    get() = getString(getColumnIndex(Phone._ID))

val Cursor.firstName: String
    @SuppressLint("Range")
    get() = getString(getColumnIndex(Phone.DISPLAY_NAME)) ?: "No name"

//val Cursor.lastName: String
//    @SuppressLint("Range")
//    get() = getString(getColumnIndex(StructuredName.FAMILY_NAME)) ?: "No name"

val Cursor.phone: String
    @SuppressLint("Range")
    get() = getString(getColumnIndex(Phone.NUMBER)) ?: "No number"

// TODO: cursor causes error because cursor is initialized for Phone.CONTENT_URI. accessing Email.DATA or other paths causes error
//val Cursor.email: String
//    @SuppressLint("Range")
//    get() = getString(getColumnIndex(Email.DATA)) ?: "No email"

val Cursor.photo: String
    @SuppressLint("Range")
    get() = getString(getColumnIndex(Phone.PHOTO_THUMBNAIL_URI)) ?: ""


fun insertContact(context: Context, contact: Contact) {
    val contentProviderOperations = arrayListOf<ContentProviderOperation>()

    contentProviderOperations.add(
        ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
            .withValue(RawContacts.ACCOUNT_TYPE, null)
            .withValue(RawContacts.ACCOUNT_NAME, null)
            .build()
    )

    // Adding first & last name
    contentProviderOperations.add(
        ContentProviderOperation
            .newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
            .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
            .withValue(StructuredName.GIVEN_NAME, contact.firstName)
            .withValue(StructuredName.FAMILY_NAME, contact.lastName)
            .build()
    )

    // Adding Number
    contentProviderOperations.add(
        ContentProviderOperation
            .newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
            .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
            .withValue(Phone.NUMBER, contact.phoneNumber)
            .withValue(Phone.TYPE, Phone.TYPE_WORK)
            .build()
    )

    // Adding email
    contentProviderOperations.add(
        ContentProviderOperation
            .newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
            .withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
            .withValue(Email.DATA, contact.email)
            .withValue(Email.TYPE, Email.TYPE_WORK)
            .build()
    )

    // Adding Photo
    contact.profileImgUrl?.let {
        Log.i("Photo save ", "successful")
        val bitmapUri = Uri.parse(it)
        val bitmap = bitmapUri.getBitmap(context.contentResolver)
        val byteArray = bitmap.toByteArray(Bitmap.CompressFormat.JPEG, 85)
        contentProviderOperations.add(
            ContentProviderOperation
                .newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                .withValue(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE)
                .withValue(Photo.PHOTO, byteArray)
                .build()
        )
        Log.i("Photo save ", "successful")
    }

    try {
        context.contentResolver.applyBatch(
            AUTHORITY,
            contentProviderOperations
        )
        Log.i("Contact save ", "successful")
    } catch (e: Exception) {
        Log.i("Contact save ", "unsuccessful")
    }
}

fun deleteContact() {

}