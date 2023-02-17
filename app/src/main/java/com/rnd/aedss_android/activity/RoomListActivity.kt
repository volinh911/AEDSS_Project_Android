package com.rnd.aedss_android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rnd.aedss_android.viewmodel.Room
import com.rnd.aedss_android.R
import com.rnd.aedss_android.adapter.RoomListAdapter
import com.rnd.aedss_android.utils.AuthenticationPreferences

class RoomListActivity : AppCompatActivity() {

    private lateinit var roomListRcv: RecyclerView
    private lateinit var roomList: ArrayList<Room>
    private lateinit var roomListAdapter: RoomListAdapter

    private lateinit var logoutBtn: ImageView

    lateinit var session: AuthenticationPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_list)

        session = AuthenticationPreferences(this)
        session.checkLogin()

        logoutBtn = findViewById(R.id.logout_btn)
        logoutBtn.setOnClickListener{
            showLogoutAlertDialog()
        }

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

    private fun showLogoutAlertDialog() {
        val dialogView = View.inflate(this, R.layout.logout_alert_dialog, null)
        val builder = android.app.AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var okBtn = dialogView.findViewById<Button>(R.id.ok_btn)
        var okSection = dialogView.findViewById<CardView>(R.id.ok_section)

        var cancelBtn = dialogView.findViewById<Button>(R.id.cancel_btn)
        var cancelSection = dialogView.findViewById<CardView>(R.id.cancel_section)

        cancelBtn.setOnClickListener{
            alertDialog.dismiss()
        }
        cancelSection.setOnClickListener{
            alertDialog.dismiss()
        }

        okSection.setOnClickListener {
            session.logoutUser()
        }
        okBtn.setOnClickListener {
            session.logoutUser()
        }

    }

}