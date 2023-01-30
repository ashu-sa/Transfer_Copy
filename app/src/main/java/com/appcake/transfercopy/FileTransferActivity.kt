package com.appcake.transfercopy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appcake.transfercopy.databinding.ActivityFileTransferBinding
import com.appcake.transfercopy.databinding.ActivityMainBinding

class FileTransferActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityFileTransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFileTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            backImg.setOnClickListener {
                startActivity(Intent(this@FileTransferActivity,MainActivity::class.java))
            }
        }
    }
}