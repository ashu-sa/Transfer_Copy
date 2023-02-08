package com.appcake.transfercopy

import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.recyclerview.widget.GridLayoutManager
import com.appcake.transfercopy.Adapter.PhotoAdapter
import com.appcake.transfercopy.databinding.ActivityPhotoBinding

class PhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = PhotoAdapter(getPhotos())
        binding.apply {
            photoRcView.layoutManager =GridLayoutManager(this@PhotoActivity,5)
            photoRcView.adapter = adapter
        }

    }
    private fun getPhotos():ArrayList<String>{
        var imageList: ArrayList<String> = ArrayList()
        val columns = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID
        )
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val imagecursor: Cursor = this@PhotoActivity.contentResolver.query(uri, columns, null, null,"")!!
        for (i in 0 until imagecursor.count) {
            imagecursor.moveToPosition(i)
            val dataColumnIndex =
                imagecursor.getColumnIndex(MediaStore.Images.Media.DATA)
            imageList.add(imagecursor.getString(dataColumnIndex))
        }
        return imageList

    }


}