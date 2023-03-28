package com.rnd.aedss_android.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rnd.aedss_android.adapter.RoomInfoViewPagerAdapter
import com.rnd.aedss_android.R
import com.rnd.aedss_android.datamodel.ConfigData
import com.rnd.aedss_android.datamodel.ResponseData
import com.rnd.aedss_android.datamodel.body_model.PostConfigBody
import com.rnd.aedss_android.datamodel.device_data.YoloData
import com.rnd.aedss_android.utils.Constants
import com.rnd.aedss_android.utils.Constants.Companion.ADD_SCHEDULE
import com.rnd.aedss_android.utils.Constants.Companion.SCHEDULE
import com.rnd.aedss_android.utils.api.RetrofitInstance
import com.rnd.aedss_android.utils.mqtt.MQTTClient
import com.rnd.aedss_android.utils.preferences.AuthenticationPreferences
import com.rnd.aedss_android.utils.preferences.RoomPreferences
import okhttp3.ResponseBody
import org.eclipse.paho.client.mqttv3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomInfoActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var addBtn: ImageView
    private lateinit var roomName: TextView
    private lateinit var configBtn: ImageView

    lateinit var roomSession: RoomPreferences
    lateinit var rcvRoom: String

    lateinit var authSession: AuthenticationPreferences
    var auth: String = ""
    var userid: String = ""

    private lateinit var mqttClient: MQTTClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_info)

        roomSession = RoomPreferences(this)
        roomName = findViewById(R.id.room_name)
        rcvRoom = intent.getStringExtra("room_name").toString()
        if (rcvRoom != null) {
            roomName.text = "Room ${rcvRoom}"
            roomSession.addRoomSession(rcvRoom)
        }

        authSession = AuthenticationPreferences(this)
        auth = authSession.getAuthToken().toString()
        userid = authSession.getUserid().toString()

        configBtn = findViewById(R.id.config_btn)

        var id: String = Constants.CLIENT_ID + "_" + (('a'..'z').random()).toString() + "_" + ((0..1000).random()).toString()
        mqttClient = MQTTClient(this, Constants.BROKER, id)

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
            intent.putExtra(SCHEDULE, ADD_SCHEDULE)
            startActivity(intent)
        }
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
        frequencyDialogOkButton.setOnClickListener {
            var selectedValue: String = hourSpinner.selectedItem.toString()
            postConfig(selectedValue)
            frequencyDialog.dismiss()
        }
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
        currentConfigDialogOkButton.setOnClickListener {
            disconnectMQTT()
            showFrequencyDialog()
            currentConfigDialog.dismiss()
        }
    }

    private fun initYoloData() {
        RetrofitInstance.apiServiceInterface.getYoloDetail(auth, userid, rcvRoom)
            .enqueue(object : Callback<List<YoloData>> {
                override fun onResponse(
                    call: Call<List<YoloData>>,
                    response: Response<List<YoloData>>
                ) {
                    if (response?.body() == null) {
                        configBtn.visibility = View.GONE
                        roomSession.addYoloSession(false)
                        Log.d("Error yolo: ", "Error")
                    } else {
                        var result = response.body()!!
//                        var publishTopic = result[0].publish
//                        var subscribeTopic = result[0].subscribe
//                        if (publishTopic != null) {
//                            if (subscribeTopic != null) {
//
//                            }
//                        }

                        roomSession.addYoloSession(true)
                        configBtn.visibility = View.VISIBLE
                        configBtn.setOnClickListener { initConfigData() }
                    }
                }

                override fun onFailure(call: Call<List<YoloData>>, t: Throwable) {
                    Log.d("Error yolo: ", "Error")
                }

            })
    }

    private fun initConfigData() {
        RetrofitInstance.apiServiceInterface.getConfig(auth, userid, rcvRoom)
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

    private fun postConfig(loopTime: String) {
        var body = PostConfigBody(userid, rcvRoom, loopTime)
        RetrofitInstance.apiServiceInterface.postConfig(auth, userid, body)
            .enqueue(object : Callback<ResponseData> {
                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    if (response?.body() == null) {
                        showAlertDialog(false)
                        Log.d("response body", "null")
                    }

                    var result = response.body()!!
                    if (result.success == false) {
                        showAlertDialog(false)
                    } else {
                        connectMQTT()
                        showAlertDialog(true)
                    }
                }

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    showAlertDialog(false)
                    Log.d("post config: ", "failure")
                }

            })
    }

    private fun showAlertDialog(success: Boolean) {
        val dialogView = View.inflate(this, R.layout.alert_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var okBtn = dialogView.findViewById<Button>(R.id.ok_btn)
        var okSection = dialogView.findViewById<CardView>(R.id.ok_section)
        var titleDialog = dialogView.findViewById<TextView>(R.id.title_dialog)
        var alertText = dialogView.findViewById<TextView>(R.id.option_dialog_text)

        okBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        okSection.setOnClickListener {
            alertDialog.dismiss()
        }

        if (success) {
            titleDialog.text = "Notification"
            alertText.text = "Set up new config check successfully."
        } else {
            alertText.text = "There is an error occurred. Please restart and try again"
        }
    }

    private fun disconnectMQTT() {
        if (mqttClient.isConnected()) {
            // Disconnect from MQTT Broker
            mqttClient.disconnect(object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(this.javaClass.name, "Disconnected")
                }
                override fun onFailure(
                    asyncActionToken: IMqttToken?,
                    exception: Throwable?
                ) {
                    Log.d(this.javaClass.name, "Failed to disconnect")
                    showAlertDialog(false)
                }
            })
        } else {
            Log.d(this.javaClass.name, "Impossible to disconnect, no server connected")
            showAlertDialog(false)
        }
    }

    private fun connectMQTT() {
        mqttClient.connect(
            Constants.USERNAME_MQTT, Constants.PASSWORD_MQTT, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d(this.javaClass.name, "Connection success")
                publishTopic()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d(this.javaClass.name, "Connection failure: ${exception.toString()}")
                showAlertDialog(false)
            }
        },
            object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    Log.d(this.javaClass.name, "Connection lost ${cause.toString()}")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val msg = "Receive message: ${message.toString()} from topic: $topic"
                    Log.d(this.javaClass.name, msg)
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d(this.javaClass.name, "Delivery complete")
                }

            })
    }

    private fun publishTopic() {
        var topic = "scheduler/server"
        var request = "config:$rcvRoom@$userid"

        mqttClient.publish(topic, request, 0, false, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                val msg = "Publish message: $request to topic $topic"
                Log.d(this.javaClass.name, msg)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d(this.javaClass.name, "Failed to publish message to topic")
            }

        })
    }
}