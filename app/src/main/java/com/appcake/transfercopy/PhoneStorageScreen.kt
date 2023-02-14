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
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.appcake.transfercopy.databinding.ActivityPhoneStorageScreenBinding
import com.appcake.transfercopy.databinding.FragmentQRcodeBinding
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

        binding.videoSpaceText.text = formatSize(videoSpace())
        val photoFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"Documents")
        val photoFolderSize = getFolderSize(photoFolder)
        binding.photoSpaceText.text = formatSize(photoFolderSize)

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
            calenderLinearLayout.setOnClickListener {
                val intent = Intent(this@PhoneStorageScreen,CalenderActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
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
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.READ_CONTACTS)
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
    private fun videoSpace():Long {
        val path = Environment.getExternalStorageDirectory().toString() + "/"
        val directory = File(path)
        val files = directory.listFiles()
        val videoFiles = files.filter {
            it.extension == "mp4" || it.extension == "3gp" || it.extension == "mkv" || it.extension == "avi"
        }
        var totalSize: Long = 0
        for (file in videoFiles) {
            totalSize += file.length()
        }
       return totalSize
    }
    fun getFolderSize(dir: File): Long {
        var size: Long = 0
        dir.listFiles()?.forEach {
            size += if (it.isDirectory) getFolderSize(it) else it.length()
        }
        return size
    }



}