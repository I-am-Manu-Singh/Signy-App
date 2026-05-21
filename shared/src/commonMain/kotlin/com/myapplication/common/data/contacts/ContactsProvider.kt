package com.myapplication.common.data.contacts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

data class ContactInfo(val name: String, val phoneNumber: String)

expect class ContactsProvider {
    suspend fun getContacts(): List<ContactInfo>
}
