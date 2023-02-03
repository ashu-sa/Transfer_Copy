package com.appcake.transfercopy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appcake.transfercopy.Fragments.QRcodeFragment
import com.appcake.transfercopy.databinding.ActivityFileTransferBinding

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
               val intent = Intent(this@FileTransferActivity,PhoneStorageScreen::class.java)
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
               startActivity(intent)
           }

            sendFileCard.setOnClickListener {
                val fragment = QRcodeFragment()
                supportFragmentManager.beginTransaction().replace(R.id.container,fragment).commit()
            }
        }
    }

}