package com.appcake.transfercopy

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcake.transfercopy.Adapter.ContactAdapter
import com.appcake.transfercopy.data.Contacts
import com.appcake.transfercopy.data.Video
import com.appcake.transfercopy.databinding.ActivityContactBinding
import java.io.File

class ContactActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactBinding
    private lateinit var contactList: ArrayList<Contacts>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        contactList = getContacts()
        val adapter = ContactAdapter(contactList)
        binding.apply {
            contactRcView.layoutManager = LinearLayoutManager(this@ContactActivity)
            contactRcView.adapter = adapter
        }


    }
    @SuppressLint("Range", "SuspiciousIndentation")
    private fun getContacts():ArrayList<Contacts>{
        var contactList: ArrayList<Contacts> = ArrayList()
        val uri = ContactsContract.Contacts.CONTENT_URI
        val contactcursor: Cursor = this@ContactActivity.contentResolver.query(uri,null, null, null, "")!!

        if (contactcursor != null)
            if (contactcursor.moveToNext())
                do {
                    val id = contactcursor.getString(contactcursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = contactcursor.getString(contactcursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                    try {
                        val contacts = Contacts(name)
                        contactList.add(contacts)

                    }catch (e:java.lang.Exception){}
                }while (contactcursor.moveToNext())
        contactcursor?.close()

        return contactList

    }
}