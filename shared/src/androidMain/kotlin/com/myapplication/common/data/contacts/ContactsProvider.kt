package com.myapplication.common.data.contacts

import android.content.Context
import android.provider.ContactsContract

actual class ContactsProvider(private val context: Context) {
    actual suspend fun getContacts(): List<ContactInfo> {
        val contacts = mutableListOf<ContactInfo>()
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )
        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)
                contacts.add(ContactInfo(name, number))
            }
        }
        return contacts
    }
}
