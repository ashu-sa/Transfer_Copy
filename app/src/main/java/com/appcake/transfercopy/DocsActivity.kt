package com.appcake.transfercopy

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.appcake.transfercopy.Adapter.DocAdapter
import com.appcake.transfercopy.data.Docs
import com.appcake.transfercopy.databinding.ActivityDocsBinding
import java.io.File

class DocsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDocsBinding
    companion object{
        lateinit var docList:ArrayList<Docs>
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        docList = getDocs()
        val adapter = DocAdapter(docList)

        binding.apply {
            docRcView.layoutManager = GridLayoutManager(this@DocsActivity,4)
            docRcView.adapter = adapter
        }


    }
    @SuppressLint("Range", "SuspiciousIndentation")
    private fun getDocs():ArrayList<Docs>{
        var docList: ArrayList<Docs> = ArrayList()
        val columns = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED

        )
        val selection = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"

        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")
        val selectionArgs = arrayOf(mimeType)
        val uri = MediaStore.Files.getContentUri("external")
        val doccursor: Cursor = this@DocsActivity.contentResolver.query(uri, columns,selection,selectionArgs,
            MediaStore.Files.FileColumns.DATE_ADDED + " DESC")!!

        if (doccursor != null)
            if (doccursor.moveToNext())
                do {
                    val id = doccursor.getString(doccursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                    val title = doccursor.getString(doccursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE))
                    val path = doccursor.getString(doccursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))

                    try {
                        val file = File(path)
                        val docs = Docs(id, title)
                        if (file.exists()) docList.add(docs)

                    }catch (e:java.lang.Exception){}
                }while (doccursor.moveToNext())
        doccursor?.close()

        return docList

    }
}