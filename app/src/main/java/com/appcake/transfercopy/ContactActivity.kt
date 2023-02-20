package com.appcake.transfercopy

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcake.transfercopy.Adapter.ContactAdapter
import com.appcake.transfercopy.data.Contacts
import com.appcake.transfercopy.data.Video
import com.appcake.transfercopy.databinding.ActivityContactBinding
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ContactActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactBinding
    private lateinit var adapter:ContactAdapter
    private lateinit var contactList: ArrayList<Contacts>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        contactList = getContacts()
        adapter = ContactAdapter(contactList)
        binding.apply {
            contactRcView.layoutManager = LinearLayoutManager(this@ContactActivity)
            contactRcView.adapter = adapter }
        binding.contactSearchview.setOnQueryTextListener(object:SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)

                return false
            }

        })
        binding.backImg.setOnClickListener {
            val intent = Intent(Intent(this@ContactActivity,PhoneStorageScreen::class.java))
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    private fun filterList(newText: String?) {
        if(newText != null){
            val filterdList = ArrayList<Contacts>()
            for (i in contactList){
                if (i.name.lowercase(Locale.ROOT).contains(newText)){
                    filterdList.add(i)
                }
            }
            if (filterdList.isEmpty()){
                binding.notFoundText.visibility = View.VISIBLE
                binding.contactRcView.visibility= View.INVISIBLE
            }else{
                binding.contactRcView.visibility= View.VISIBLE
                binding.notFoundText.visibility = View.INVISIBLE
                adapter.setFilteredList(filterdList)
            }
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