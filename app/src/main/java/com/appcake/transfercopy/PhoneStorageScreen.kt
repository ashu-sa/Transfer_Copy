package com.appcake.transfercopy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.appcake.transfercopy.databinding.ActivityPhoneStorageScreenBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.util.*

class PhoneStorageScreen : AppCompatActivity() {
    private lateinit var binding: ActivityPhoneStorageScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPhoneStorageScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validatePermission()

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
                val intent = Intent(this@PhoneStorageScreen,ContactActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
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
                val intent = Intent(this@PhoneStorageScreen,CalenderActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
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
    private fun validatePermission() {
        Dexter.withContext(this)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.READ_CONTACTS,android.Manifest.permission.READ_CALENDAR,android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {

                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    Toast.makeText(this@PhoneStorageScreen, "Permission 3", Toast.LENGTH_SHORT).show()
                }

            }).check()
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

       // Query the media provider to retrieve the size of all docs
        var totalSize = 0L
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE))
                totalSize += size
            }
        }
        cursor?.close()

        return totalSize
    }



}