package com.appcake.transfercopy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appcake.transfercopy.databinding.ActivityCalenderBinding

class CalenderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalenderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalenderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.allEventFilesText.setOnClickListener {
            val intent = Intent(this@CalenderActivity,AllEventsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
