package com.appcake.transfercopy

import android.content.Intent
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
            photoRcView.layoutManager =GridLayoutManager(this@PhotoActivity,4
            )
            photoRcView.adapter = adapter
        }
        binding.backImg.setOnClickListener {
            val intent = Intent(Intent(this@PhotoActivity,PhoneStorageScreen::class.java))
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }
    private fun getPhotos():ArrayList<String>{
        var imageList: ArrayList<String> = ArrayList()
        val columns = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED

        )
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val imagecursor: Cursor = this@PhotoActivity.contentResolver.query(uri, columns, null, null,  MediaStore.Images.Media.DATE_ADDED + " DESC")!!
        for (i in 0 until imagecursor.count) {
            imagecursor.moveToPosition(i)
            val dataColumnIndex =
                imagecursor.getColumnIndex(MediaStore.Images.Media.DATA)
            imageList.add(imagecursor.getString(dataColumnIndex))
        }
        return imageList

    }


}