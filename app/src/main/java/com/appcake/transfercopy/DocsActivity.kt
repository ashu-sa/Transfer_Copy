package com.appcake.transfercopy

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.appcake.transfercopy.Adapter.DocAdapter
import com.appcake.transfercopy.data.Docs
import com.appcake.transfercopy.databinding.ActivityDocsBinding
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class DocsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDocsBinding
    companion object{
        lateinit var docList:ArrayList<Docs>
        lateinit var pdfList:ArrayList<File>
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        docList = getDocs()
        pdfList = findPdf(Environment.getDataDirectory().absoluteFile)
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
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATE_ADDED

        )
        val selection = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"
        val mimeType = arrayOf("application/pdf")
        val selectionArgs = arrayOf(mimeType)
        val uri = MediaStore.Files.getContentUri("external")
        val doccursor: Cursor = this@DocsActivity.contentResolver.query(uri,columns,null,null,
            MediaStore.Files.FileColumns.DATE_ADDED + " DESC")!!

        if (doccursor != null)
            if (doccursor.moveToNext())
                do {
                    val id = doccursor.getString(doccursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                    val title = doccursor.getString(doccursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME))
                    val path = doccursor.getString(doccursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                        try {
                                val docs = Docs(id, title)
                                docList.add(docs)



                    }catch (e:java.lang.Exception){}
                }while (doccursor.moveToNext())
        doccursor?.close()

        return docList

    }
    private fun checkOtherFileType(filePath: String): Boolean? {
        if (!filePath.isNullOrEmpty()) {
            val filePathInLowerCase = filePath.lowercase(Locale.getDefault())
            if (filePathInLowerCase.endsWith(".pdf")) {
                return true
            }
        }
        return false
    }
    fun findPdf(file: File): ArrayList<File> {
        val arrayList = arrayListOf<File>()
        val files = file.listFiles()
            if (files != null) {
                for (singleFile in files) {
                    if (singleFile.isDirectory && !singleFile.isHidden) {
                        arrayList.addAll(findPdf(singleFile))
                    } else {
                        if (singleFile.name.endsWith(".pdf")) {
                            arrayList.add(singleFile)
                        }
                    }
                }
            }
            return arrayList
        }

}