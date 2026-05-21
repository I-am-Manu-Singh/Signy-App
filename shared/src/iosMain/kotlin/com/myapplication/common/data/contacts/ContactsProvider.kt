package com.myapplication.common.data.contacts

actual class ContactsProvider {
    actual suspend fun getContacts(): List<ContactInfo> {
        return emptyList()
    }
}
