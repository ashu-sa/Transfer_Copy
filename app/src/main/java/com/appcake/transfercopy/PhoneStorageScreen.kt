package com.appcake.transfercopy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.view.View
import android.widget.Toast
import com.appcake.transfercopy.databinding.ActivityPhoneStorageScreenBinding
import com.appcake.transfercopy.databinding.FragmentQRcodeBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File

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
            .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(this@PhoneStorageScreen, "Permission 2", Toast.LENGTH_SHORT).show()

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
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


}