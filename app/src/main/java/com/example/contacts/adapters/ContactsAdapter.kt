package com.example.contacts.adapters

import android.database.Cursor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.databinding.ListItemContactBinding
import com.example.contacts.domain.Contact
import com.example.contacts.util.firstName
import com.example.contacts.util.phone
import com.example.contacts.util.photo

class ContactsAdapter(private var cursor: Cursor) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {
    class ViewHolder private constructor(private val binding: ListItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Contact,
        ) {
            binding.contact = item
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemContactBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor.moveToPosition(position)
        val contact = Contact(cursor.firstName, "", cursor.photo, cursor.phone, "")
        holder.bind(contact)
    }

    override fun getItemCount() = cursor.count

    fun submitCursor(cursor: Cursor) {
        this.cursor = cursor
        notifyDataSetChanged()
    }
}