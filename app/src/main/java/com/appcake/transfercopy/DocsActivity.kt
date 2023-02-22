package com.appcake.transfercopy

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.appcake.transfercopy.Adapter.DocAdapter
import com.appcake.transfercopy.data.Docs
import com.appcake.transfercopy.databinding.ActivityDocsBinding
import java.util.*
import kotlin.collections.ArrayList


class DocsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDocsBinding
    private lateinit var adapter: DocAdapter
    companion object {
        lateinit var docList: ArrayList<Docs>
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        docList = getAllDocs()
        adapter = DocAdapter(docList)
        binding.apply {
            docRcView.layoutManager = GridLayoutManager(this@DocsActivity,3)
            docRcView.adapter = adapter
        }
        binding.docSearchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
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
            val intent = Intent(Intent(this@DocsActivity,PhoneStorageScreen::class.java))
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun filterList(newText: String?) {
        if(newText != null){
            val filterdList = ArrayList<Docs>()
            for (i in docList){
                if (i.title.lowercase(Locale.ROOT).contains(newText)){
                    filterdList.add(i)
                }
            }
            if (filterdList.isEmpty()){
                binding.notFoundText.visibility = View.VISIBLE
                binding.docRcView.visibility= View.INVISIBLE
            }else{
                binding.docRcView.visibility= View.VISIBLE
                binding.notFoundText.visibility = View.INVISIBLE
                adapter.setFilteredList(filterdList)
            }
        }

    }
    private fun getAllDocs(): ArrayList<Docs> {
        val docList: ArrayList<Docs> = ArrayList()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager().not()) {
        // Request the MANAGE_EXTERNAL_STORAGE permission
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.data = Uri.parse("package:${applicationContext.packageName}")
       startActivity(intent)
    }else {

        val projection = arrayOf(
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_ADDED
        )

        val selection = "${MediaStore.Files.FileColumns.DATA} LIKE ? " +
                "OR ${MediaStore.Files.FileColumns.DATA} LIKE ? " +
                "OR ${MediaStore.Files.FileColumns.DATA} LIKE ? "
        val selectionArgs = arrayOf("%${".pdf"}", "%${".docx"}", "%${".doc"}")

        val uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)

        applicationContext.contentResolver.query(uri, projection,selection,selectionArgs,MediaStore.Files.FileColumns.DATE_ADDED+ " DESC")?.use { cursor ->
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

            while (cursor.moveToNext()) {
                val path = cursor.getString(dataColumn)
                val name = cursor.getString(titleColumn)
                val size = cursor.getLong(sizeColumn)

                if (path.endsWith(".pdf") || path.endsWith(".doc") || path.endsWith(".docx")) {
                    val docs = Docs(name, path, size)
                    docList.add(docs)
                }
            }
        }
     }
    return docList
    }

}

