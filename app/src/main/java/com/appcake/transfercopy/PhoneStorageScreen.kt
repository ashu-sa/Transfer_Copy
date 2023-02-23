package com.appcake.transfercopy

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.appcake.transfercopy.databinding.ActivityPhoneStorageScreenBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.util.*

class PhoneStorageScreen : AppCompatActivity() {
    private lateinit var binding: ActivityPhoneStorageScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPhoneStorageScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val iPath: File = Environment.getDataDirectory()
        val iStat = StatFs(iPath.path)
        val iBlockSize = iStat.blockSizeLong
        val iAvailableBlocks = iStat.availableBlocksLong
        val iTotalBlocks = iStat.blockCountLong
        var iAvailableSpace = formatSize(iAvailableBlocks * iBlockSize)
        var iTotalSpace = formatSize(iTotalBlocks * iBlockSize)
        binding.apply {
            totalStorageText.text = "$iTotalSpace"
            availableStorageText.text = "$iAvailableSpace"
        }

        //Progress bar
        iAvailableSpace = iAvailableSpace?.replace(" GB","")
        iTotalSpace = iTotalSpace?.replace(" GB","")
        val totalSpaceInt = iTotalSpace!!.toInt()
        val availableSpaceInt= iAvailableSpace!!.toInt()
        val remainingSpaceInt = totalSpaceInt - availableSpaceInt

        setProgressBar(totalSpaceInt,remainingSpaceInt)


        binding.apply {
            contactsLinearLayout.setOnClickListener {
                val permission = android.Manifest.permission.READ_CONTACTS
                if (ContextCompat.checkSelfPermission(this@PhoneStorageScreen, permission) == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(this@PhoneStorageScreen,ContactActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@PhoneStorageScreen, "Please Grant Contact Permissions Manually to use the App!", Toast.LENGTH_SHORT).show()
                }

            }
            docsLinearLayout.setOnClickListener {
                val intent = Intent(this@PhoneStorageScreen,DocsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            videosLinearLayout.setOnClickListener {
                val intent = Intent(this@PhoneStorageScreen,VideoActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            photosLinearLayout.setOnClickListener {
                val intent = Intent(this@PhoneStorageScreen,PhotoActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            backImg.setOnClickListener {
                val intent = Intent(Intent(this@PhoneStorageScreen,FileTransferActivity::class.java))
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            calenderLinearLayout.setOnClickListener {
                val permission = android.Manifest.permission.READ_CALENDAR
                if (ContextCompat.checkSelfPermission(this@PhoneStorageScreen, permission) == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(this@PhoneStorageScreen,CalenderActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@PhoneStorageScreen, "Please Grant Calendar Permissions Manually to use the App!", Toast.LENGTH_SHORT).show()
                }
            }
            calendarSizeText.text = "0 GB/ $iTotalSpace GB"
            contactSizeText.text = "0 GB/ $iTotalSpace GB"
            photoSpaceText.text = formatSize(photoSpace()) +"/ $iTotalSpace GB "
            videoSpaceText.text = formatSize(videoSpace()) +"/ $iTotalSpace GB "
            docSizeText.text = formatSize(docSpace()) +"/ $iTotalSpace GB "
        }

    }
    private fun formatSize(size: Long): String? {
        var size = size
        var suffix: String? = null
        if (size >= 1024) {
            suffix = " KB"
            size /= 1024
            if (size >= 1024) {
                suffix = " MB"
                size /= 1024
                 if (size >= 1024) {
                    suffix = " GB"
                    size /= 1024
                }
            }


        }
        val resultBuffer = StringBuilder(java.lang.Long.toString(size))
        var commaOffset = resultBuffer.length - 3
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',')
            commaOffset -= 3
        }
        if (suffix != null) resultBuffer.append(suffix)
        return resultBuffer.toString()
    }
    private fun setProgressBar(max:Int,progress:Int){
        val max = max
        val progress = progress
        binding.apply {
            progressBar.progress = progress
            progressBar.max = max
        }
    }
    private fun photoSpace():Long {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media.SIZE)

       // Query the media provider to retrieve the size of all photos
        var totalSize = 0L
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
                totalSize += size
            }
        }
        cursor?.close()

        return totalSize
    }
    private fun videoSpace():Long {
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Video.Media.SIZE)

       // Query the media provider to retrieve the size of all videos
        var totalSize = 0L
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                totalSize += size
            }
        }
        cursor?.close()

        return totalSize
    }
    private fun docSpace():Long {
        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(MediaStore.Files.FileColumns.SIZE)
        val selection = "${MediaStore.Files.FileColumns.DATA} LIKE ? " +
                "OR ${MediaStore.Files.FileColumns.DATA} LIKE ? " +
                "OR ${MediaStore.Files.FileColumns.DATA} LIKE ? "
        val selectionArgs = arrayOf("%${".pdf"}", "%${".docx"}", "%${".doc"}")

       // Query the media provider to retrieve the size of all docs
        var totalSize = 0L
        val cursor = contentResolver.query(uri, projection,selection,selectionArgs, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE))
                totalSize += size
            }
        }
        cursor?.close()

        return totalSize
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(Intent(this@PhoneStorageScreen,FileTransferActivity::class.java))
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }
}