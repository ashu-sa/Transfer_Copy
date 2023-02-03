package com.appcake.transfercopy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import com.appcake.transfercopy.databinding.ActivityPhoneStorageScreenBinding
import com.appcake.transfercopy.databinding.FragmentQRcodeBinding
import java.io.File

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


}