package com.appcake.transfercopy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.appcake.transfercopy.databinding.ActivityFileTransferBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class FileTransferActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityFileTransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFileTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            backImg.setOnClickListener {
                val intent = Intent(Intent(this@FileTransferActivity,MainActivity::class.java))
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

           receiveFileCard.setOnClickListener {
               val intent = Intent(this@FileTransferActivity,ReceiveActivity::class.java)
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
               startActivity(intent)
           }
        }
    }
}