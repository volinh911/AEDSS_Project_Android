//adapter for tablayout in RoomInfoActivity

package com.rnd.aedss_android.adapter

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rnd.aedss_android.fragment.StatusInfoFragment
import com.rnd.aedss_android.fragment.TimeListInfoFragment

class RoomInfoViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity)
{
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {StatusInfoFragment() }
            1 -> {TimeListInfoFragment() }
            else -> { throw Resources.NotFoundException("Position not found")}
        }
    }
}