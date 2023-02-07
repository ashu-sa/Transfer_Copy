package com.appcake.transfercopy.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.appcake.transfercopy.Fragments.*

private const val NUM_TABS = 12

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return JanuaryFragment()
            1 -> return FebruaryFragment()
            2 -> return MarchFragment()
            3 -> return AprilFragment()
            4 -> return MayFragment()
            5 -> return JuneFragment()
            6 -> return JulyFragment()
            7 -> return AugustFragment()
            8 -> return SeptemberFragment()
            9 -> return OctoberFragment()
            10 -> return NovemberFragment()
            11 -> return DecemberFragment()
        }
        return JanuaryFragment()
    }
}