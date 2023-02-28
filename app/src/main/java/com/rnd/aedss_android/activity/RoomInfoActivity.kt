package com.rnd.aedss_android.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rnd.aedss_android.adapter.RoomInfoViewPagerAdapter
import com.rnd.aedss_android.R
import com.rnd.aedss_android.datamodel.ConfigData
import com.rnd.aedss_android.datamodel.device_data.YoloData
import com.rnd.aedss_android.utils.Constants
import com.rnd.aedss_android.utils.api.RetrofitInstance
import com.rnd.aedss_android.utils.preferences.RoomPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomInfoActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var addBtn: ImageView
    private lateinit var roomName: TextView
    private lateinit var configBtn: ImageView

    lateinit var session: RoomPreferences
    lateinit var rcvRoom: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_info)

        session = RoomPreferences(this)

        roomName = findViewById(R.id.room_name)
        rcvRoom = intent.getStringExtra("room_name").toString()
        if (rcvRoom != null) {
            roomName.text = "Room ${rcvRoom}"
            session.addRoomSession(rcvRoom)
        }

        initYoloData()

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = RoomInfoViewPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, index ->
            tab.text = when (index) {
                0 -> {
                    "status"
                }
                1 -> {
                    "list"
                }
                else -> {
                    throw Resources.NotFoundException("Position not found")
                }
            }
        }.attach()

        addBtn = findViewById(R.id.add_btn)
        addBtn.setOnClickListener {
            val intent = Intent(this, EditTimeActivity::class.java)
            intent.putExtra("title", "Add Time")
            startActivity(intent)
        }

        configBtn = findViewById(R.id.config_btn)
        configBtn.setOnClickListener { showFrequencyDialog() }
    }

    private fun showFrequencyDialog() {
        val dialogView = View.inflate(this, R.layout.frequency_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val frequencyDialog = builder.create()
        frequencyDialog.show()
        frequencyDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var hourSpinner = dialogView.findViewById<Spinner>(R.id.hour_spinner)
        val spinnerAdapter =
            this?.let {
                ArrayAdapter<String>(
                    it,
                    R.layout.hour_spinner_item,
                    Constants.createHourList()
                )
            }
        hourSpinner.adapter = spinnerAdapter

        var frequencyDialogTitle = dialogView.findViewById<TextView>(R.id.frequency_dialog_title)
        frequencyDialogTitle.setText("CONFIG CAMERA")

        var frequencyDialogCancelButton = dialogView.findViewById<Button>(R.id.cancel_btn)
        frequencyDialogCancelButton.setOnClickListener {
            frequencyDialog.dismiss()
        }
        var frequencyDialogOkButton = dialogView.findViewById<Button>(R.id.ok_btn)
    }

    private fun showCurrentConfigDialog(configText: String) {
        val dialogView = View.inflate(this, R.layout.current_config_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val currentConfigDialog = builder.create()
        currentConfigDialog.show()
        currentConfigDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var currentConfigText = dialogView.findViewById<TextView>(R.id.current_config_text)
        currentConfigText.text = configText

        var currentConfigDialogCancelButton = dialogView.findViewById<Button>(R.id.cancel_btn)
        currentConfigDialogCancelButton.setOnClickListener {
            currentConfigDialog.dismiss()
        }
        var currentConfigDialogOkButton = dialogView.findViewById<Button>(R.id.ok_btn)
    }

    private fun initYoloData() {
        RetrofitInstance.apiServiceInterface.getYoloDetail(rcvRoom)
            .enqueue(object : Callback<List<YoloData>> {
                override fun onResponse(
                    call: Call<List<YoloData>>,
                    response: Response<List<YoloData>>
                ) {
                    if (response?.body() == null) {
                        configBtn.visibility = View.VISIBLE
                        Log.d("Error yolo: ", "Error")
                    }
                    session.addYoloSession()
                    var result = response.body()
                    configBtn.visibility = View.VISIBLE
                    configBtn.setOnClickListener { initConfigData() }
                }

                override fun onFailure(call: Call<List<YoloData>>, t: Throwable) {
                    Log.d("Error yolo: ", "Error")
                }

            })
    }

    private fun initConfigData() {
        RetrofitInstance.apiServiceInterface.getConfig(rcvRoom)
            .enqueue(object : Callback<ConfigData> {
                override fun onResponse(call: Call<ConfigData>, response: Response<ConfigData>) {
                    if (response?.body() == null) {
                        showFrequencyDialog()
                        Log.d("Error config: ", "Error")
                    }

                    var result = response.body()!!
                    showCurrentConfigDialog(result.loopTime.toString())
                }

                override fun onFailure(call: Call<ConfigData>, t: Throwable) {
                    Log.d("Error config: ", "Error")
                }
            })
    }
}