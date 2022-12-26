package com.rnd.aedss_android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rnd.aedss_android.Model.Room
import com.rnd.aedss_android.R
import com.rnd.aedss_android.adapter.RoomListAdapter

class RoomListActivity : AppCompatActivity() {

    private lateinit var roomListRcv: RecyclerView
    private lateinit var roomList: ArrayList<Room>
    private lateinit var roomListAdapter: RoomListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_list)

        roomListRcv = findViewById(R.id.list_room)
        roomListRcv.setHasFixedSize(true)
        roomListRcv.layoutManager = LinearLayoutManager(this)
        roomList = ArrayList()
        roomList.add(Room("Room 11C"))
        roomList.add(Room("Room 11A"))
        roomListAdapter = RoomListAdapter(roomList)
        roomListRcv.adapter = roomListAdapter

        roomListAdapter.onRoomClick = {
            val intent = Intent(this, RoomInfoActivity::class.java)
            intent.putExtra("room", it)
            startActivity(intent)
        }
    }
}