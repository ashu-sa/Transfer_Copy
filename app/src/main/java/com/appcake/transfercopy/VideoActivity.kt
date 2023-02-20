package com.appcake.transfercopy

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.recyclerview.widget.GridLayoutManager
import com.appcake.transfercopy.Adapter.ContactAdapter
import com.appcake.transfercopy.Adapter.VideoAdapter
import com.appcake.transfercopy.data.Video
import com.appcake.transfercopy.databinding.ActivityVideoBinding
import java.io.File

class VideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoBinding
    companion object{
        lateinit var videoList:ArrayList<Video>
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoList = getVideos()

        val adapter = VideoAdapter(videoList)
        binding.apply {
            videoRcView.layoutManager = GridLayoutManager(this@VideoActivity,4)
            videoRcView.adapter = adapter
        }
        binding.backImg.setOnClickListener {
            val intent = Intent(Intent(this@VideoActivity,PhoneStorageScreen::class.java))
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }
    @SuppressLint("Range", "SuspiciousIndentation")
    private fun getVideos():ArrayList<Video>{
        var videoList: ArrayList<Video> = ArrayList()
        val columns = arrayOf(
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATE_ADDED
        )
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val videocursor: Cursor = this@VideoActivity.contentResolver.query(uri, columns, null, null,MediaStore.Video.Media.DATE_ADDED + " DESC")!!

       if (videocursor != null)
           if (videocursor.moveToNext())
               do {
                   val id = videocursor.getString(videocursor.getColumnIndex(MediaStore.Video.Media._ID))
                   val duration = videocursor.getString(videocursor.getColumnIndex(MediaStore.Video.Media.DURATION)).toLong()
                   val path = videocursor.getString(videocursor.getColumnIndex(MediaStore.Video.Media.DATA))

                   try {
                       val file = File(path)
                       val artUri = Uri.fromFile(file)
                       val video = Video(id, duration, path, artUri)
                       if (file.exists()) videoList.add(video)

                   }catch (e:java.lang.Exception){}
               }while (videocursor.moveToNext())
               videocursor?.close()

        return videoList

    }
}