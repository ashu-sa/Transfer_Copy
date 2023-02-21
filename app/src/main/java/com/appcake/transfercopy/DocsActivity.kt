package com.appcake.transfercopy

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
        lateinit var pdfList: ArrayList<String>
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        docList = getAllDocs()
        pdfList = getPdfs()
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

    private fun getDocs(context: Context):List<Docs>{
        val pdfFiles = ArrayList<Docs>()
        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.SIZE
        )
        val selection = "${MediaStore.Files.FileColumns.DATA} LIKE ?"
        val selectionArgs = arrayOf("%${".docx"}")

        context.contentResolver.query(uri, projection,selection,selectionArgs, null)?.use { cursor ->
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

            while (cursor.moveToNext()) {
                val path = cursor.getString(dataColumn)
                val name = cursor.getString(titleColumn)
                val size = cursor.getLong(sizeColumn)

                if (path.endsWith(".pdf") || path.endsWith(".doc") || path.endsWith(".docx")) {
                    pdfFiles.add(Docs(name, path, size))
                }
            }
        }

        return pdfFiles
    }
    private fun getPdfs():ArrayList<String>{
        val selection = MediaStore.Files.FileColumns.MIME_TYPE+"=?"
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")
        val selectionArgs = arrayOf(mimeType)
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)

        } else {
            MediaStore.Files.getContentUri("external")
        }
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE)
        val docCursor: Cursor = this.contentResolver.query(uri,projection,null,null,null)!!
        val pdfFiles = ArrayList<String>()
        if (docCursor != null){
            while (docCursor.moveToNext()){
                val index = docCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                val path = docCursor.getString(index)
                pdfFiles.add(path)
            }
            docCursor.close()
        }
      return pdfFiles

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
    private fun getAllDocs():ArrayList<Docs>{
        var docList: ArrayList<Docs> = ArrayList()
        val columns = arrayOf(
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE
        )
        val uri = MediaStore.Files.getContentUri("external")
        val cursor: Cursor = this@DocsActivity.contentResolver.query(uri,columns,null,null,"")!!

        if (cursor != null)
            if (cursor.moveToNext())
                do {
                    val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                    val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                    val path = cursor.getString(dataColumn)
                    val name = cursor.getString(titleColumn)
                    val size = cursor.getLong(sizeColumn)

                    try {
                        val docs = Docs(name,path,size)
                        docList.add(docs)
                    }catch (e:java.lang.Exception){}
                }while (cursor.moveToNext())
        cursor?.close()

        return docList
    }

}

