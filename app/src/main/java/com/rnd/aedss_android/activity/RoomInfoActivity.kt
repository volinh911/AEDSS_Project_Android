package com.rnd.aedss_android.activity

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rnd.aedss_android.viewmodel.Room
import com.rnd.aedss_android.adapter.RoomInfoViewPagerAdapter
import com.rnd.aedss_android.R

class RoomInfoActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var addBtn: ImageView
    private lateinit var roomName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_info)

        roomName = findViewById(R.id.room_name)
        val rcvRoom = intent.getParcelableExtra<Room>("room")
        if (rcvRoom != null) {
            roomName.text = rcvRoom.name
        }

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = RoomInfoViewPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) {tab, index ->
            tab.text = when(index) {
                0 -> {"status"}
                1 -> {"list"}
                else -> { throw Resources.NotFoundException("Position not found")}
            }
        }.attach()

        addBtn = findViewById(R.id.add_btn)
        addBtn.setOnClickListener {
            val intent = Intent(this, EditTimeActivity::class.java)
            intent.putExtra("title", "Add Time")
            startActivity(intent)
        }

    }
}