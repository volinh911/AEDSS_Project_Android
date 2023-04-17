package com.rnd.aedss_android.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
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

        askNotificationPermission()
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
    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                return
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }
}