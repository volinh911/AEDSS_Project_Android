package com.rnd.aedss_android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.rnd.aedss_android.viewmodel.Room
import com.rnd.aedss_android.R
import com.rnd.aedss_android.activity.user_activity.ChangePasswordActivity
import com.rnd.aedss_android.adapter.RoomListAdapter
import com.rnd.aedss_android.datamodel.RoomData
import com.rnd.aedss_android.utils.Constants
import com.rnd.aedss_android.utils.preferences.AuthenticationPreferences
import com.rnd.aedss_android.utils.api.RetrofitInstance
import com.rnd.aedss_android.utils.mqtt.MQTTClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomListActivity : AppCompatActivity() {

    private lateinit var roomListRcv: RecyclerView
    var roomList: MutableList<Room> = mutableListOf()

    private lateinit var logoutBtn: ImageView
    private lateinit var changePasswordBtn: ImageView

    lateinit var session: AuthenticationPreferences
    var auth: String = ""
    var userid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_list)

        session = AuthenticationPreferences(this)
        session.checkLogin()

        auth = session.getAuthToken().toString()
        userid = session.getUserid().toString()

        subscribeTopics(userid)

        logoutBtn = findViewById(R.id.logout_btn)
        logoutBtn.setOnClickListener {
            showAlertDialog()
        }

        roomListRcv = findViewById(R.id.list_room)
        roomListRcv.setHasFixedSize(true)
        roomListRcv.layoutManager = LinearLayoutManager(this)
        roomListRcv.adapter = RoomListAdapter(applicationContext, roomList)
        initData()

        changePasswordBtn = findViewById(R.id.change_password_btn)
        changePasswordBtn.setOnClickListener {
            var intent: Intent = Intent(applicationContext, ChangePasswordActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun showAlertDialog() {
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

        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        cancelSection.setOnClickListener {
            alertDialog.dismiss()
        }

        okSection.setOnClickListener {
            session.logoutUser()
        }
        okBtn.setOnClickListener {
            session.logoutUser()
        }

    }

    fun initData() {
        RetrofitInstance.apiServiceInterface.getAllRooms(auth, userid)
            .enqueue(object : Callback<RoomData> {
                override fun onResponse(call: Call<RoomData>, response: Response<RoomData>) {
                    if (response?.body() == null) {
                        return
                    }
                    var result = response.body()!!
                    for (room in result.uniqueRoom) {
                        roomList.add(Room(room))
                    }
                    roomListRcv.adapter?.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<RoomData>, t: Throwable) {
                    Log.d("Error Room List: ", "Error")
                }
            })
    }

    fun subscribeTopics(userid: String) {
        Firebase.messaging.subscribeToTopic(userid)
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d("fb subscribe topic", msg)
            }
    }
}