package com.appcake.transfercopy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appcake.transfercopy.Adapter.ViewPagerAdapter
import com.appcake.transfercopy.databinding.ActivityAllEventsBinding
import com.google.android.material.tabs.TabLayoutMediator

class AllEventsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllEventsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAllEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fgArray = arrayOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "June",
            "July",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        val adapter = ViewPagerAdapter(supportFragmentManager,lifecycle)
        viewPager.adapter = adapter

        binding.backImg.setOnClickListener {
            val intent = Intent(Intent(this@AllEventsActivity,PhoneStorageScreen::class.java))
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        TabLayoutMediator(tabLayout,viewPager){tab,position->
            tab.text = fgArray[position]
        }.attach()
    }
}