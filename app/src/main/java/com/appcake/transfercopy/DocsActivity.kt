package com.appcake.transfercopy

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
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
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        docList = getDocs()
        pdfList = findPdf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
        val adapter = DocAdapter(docList)
        binding.apply {
            docRcView.layoutManager = GridLayoutManager(this@DocsActivity,3)
            docRcView.adapter = adapter
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("Range", "SuspiciousIndentation")
    private fun getDocs():ArrayList<Docs>{
        var docList: ArrayList<Docs> = ArrayList()
        val columns = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE
        )
        val uri = MediaStore.Files.getContentUri("external")
        val doccursor: Cursor = this@DocsActivity.contentResolver.query(uri,columns,null,null,"")!!

        if (doccursor != null)
            if (doccursor.moveToNext())
                do {
                    val id = doccursor.getString(doccursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                    val title = doccursor.getString(doccursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME))
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

