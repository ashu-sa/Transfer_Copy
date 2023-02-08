package com.appcake.transfercopy

import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.recyclerview.widget.GridLayoutManager
import com.appcake.transfercopy.Adapter.VideoAdapter
import com.appcake.transfercopy.databinding.ActivityVideoBinding

class VideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = VideoAdapter(getVideos())
        binding.apply {
            videoRcView.layoutManager = GridLayoutManager(this@VideoActivity,5)
            videoRcView.adapter = adapter
        }

    }
    private fun getVideos():ArrayList<String>{
        var imageList: ArrayList<String> = ArrayList()
        val columns = arrayOf(
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media._ID
        )
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val imagecursor: Cursor = this@VideoActivity.contentResolver.query(uri, columns, null, null,"")!!
        for (i in 0 until imagecursor.count) {
            imagecursor.moveToPosition(i)
            val dataColumnIndex =
                imagecursor.getColumnIndex(MediaStore.Video.Media.DATA)
            imageList.add(imagecursor.getString(dataColumnIndex))
        }
        return imageList

    }
}