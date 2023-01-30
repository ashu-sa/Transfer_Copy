package com.appcake.transfercopy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appcake.transfercopy.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private  lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            homeImg.setOnClickListener {
                startActivity(Intent(this@SettingActivity,MainActivity::class.java))
            }
        }
    }
}