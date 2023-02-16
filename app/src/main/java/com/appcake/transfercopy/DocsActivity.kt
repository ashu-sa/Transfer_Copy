package com.appcake.transfercopy

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
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
        lateinit var pdfList:ArrayList<Docs>
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        docList = getDocs()
        pdfList = findPdf(File("/"))
        val adapter = DocAdapter(pdfList)

        binding.apply {
            docRcView.layoutManager = GridLayoutManager(this@DocsActivity,4)
            docRcView.adapter = adapter
        }


    }
    @SuppressLint("Range", "SuspiciousIndentation")
    private fun getDocs():ArrayList<Docs>{
        var docList: ArrayList<Docs> = ArrayList()
        val columns = arrayOf(
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_SIZE,
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,

        )
        val documentMimeTypes = arrayOf(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )

        // Query the Documents Provider to retrieve all document files
        val selection = "${DocumentsContract.Document.COLUMN_MIME_TYPE} IN (?, ?, ?, ?, ?, ?, ?)"
        val selectionArgs = documentMimeTypes
        val uri = MediaStore.Files.getContentUri("external")
        val doccursor: Cursor = this@DocsActivity.contentResolver.query(uri,columns,null,null,"")!!

        if (doccursor != null)
            if (doccursor.moveToNext())
                do {
                    val id = doccursor.getString(doccursor.getColumnIndex( DocumentsContract.Document.COLUMN_DOCUMENT_ID))
                    val title = doccursor.getString(doccursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME))
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
    fun findPdf(file: File): ArrayList<Docs> {
        val extensions = arrayOf(".pdf", ".docx", ".ppt", ".xls")
        val files = ArrayList<Docs>()
        val stack = Stack<File>()
        stack.push(file)

        while (stack.isNotEmpty()) {
            val dir = stack.pop()
            val fileList = dir.listFiles()
            if (fileList != null) {
                for (file in fileList) {
                    if (file.isDirectory) {
                        stack.push(file)
                    } else {
                        for (extension in extensions) {
                            if (file.name.endsWith(extension)) {
                                files.add(Docs( file.path, file.name))
                                break
                            }
                        }
                    }
                }
            }
        }
        return files
    }

}

