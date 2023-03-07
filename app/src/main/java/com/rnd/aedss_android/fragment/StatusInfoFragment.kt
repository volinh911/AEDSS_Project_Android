package com.rnd.aedss_android.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.rnd.aedss_android.R
import com.rnd.aedss_android.activity.GalleryActivity
import com.rnd.aedss_android.activity.RoomListActivity
import com.rnd.aedss_android.datamodel.device_data.*
import com.rnd.aedss_android.utils.Constants.Companion.BROKER
import com.rnd.aedss_android.utils.Constants.Companion.CLIENT_ID
import com.rnd.aedss_android.utils.Constants.Companion.PASSWORD_MQTT
import com.rnd.aedss_android.utils.Constants.Companion.REQUEST_AC_STATE
import com.rnd.aedss_android.utils.Constants.Companion.REQUEST_DOOR
import com.rnd.aedss_android.utils.Constants.Companion.REQUEST_LIGHT_STATE
import com.rnd.aedss_android.utils.Constants.Companion.USERNAME_MQTT
import com.rnd.aedss_android.utils.api.RetrofitInstance
import com.rnd.aedss_android.utils.mqtt.MQTTClient
import com.rnd.aedss_android.utils.preferences.RoomPreferences
import org.eclipse.paho.client.mqttv3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.concurrent.schedule
import kotlin.system.exitProcess

class StatusInfoFragment : Fragment() {

    private lateinit var acSection: LinearLayout
    private lateinit var acStatus: ImageView
    private lateinit var lightSection: LinearLayout
    private lateinit var lightStatus: ImageView
    private lateinit var doorSection: LinearLayout
    private lateinit var doorStatus: ImageView
    private lateinit var gallerySection: LinearLayout

    lateinit var roomSession: RoomPreferences
    lateinit var rcvRoom: String
    var haveYolo: Boolean = false

    var deviceList: MutableList<String> = mutableListOf() //function in api
    var topicList: MutableList<TopicData> = mutableListOf()

    //subscribe topic
    var acSubscribeTopic: String = ""
    var lightSubscribeTopic: String = ""
    var doorSubscribeTopic: String = ""
    var isSubscribeAc: Boolean = false
    var isSubscribeLight: Boolean = false
    var isSubscribeDoor: Boolean = false

    var countDevice: Int = 0

    //publish topic
    var acPublishTopic: String = ""
    var lightPublishTopic: String = ""
    var doorPublishTopic: String = ""
    var isPublishAc: Boolean = false
    var isPublishLight: Boolean = false
    var isPublishDoor: Boolean = false

    private lateinit var mqttClient: MQTTClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                disconnectMQTT()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_status_info, container, false)

        roomSession = context?.let { RoomPreferences(it) }!!
        rcvRoom = roomSession.getRoomName()!!
        haveYolo = roomSession.doesHaveYolo()!!

        acSection = view.findViewById(R.id.ac_section)
        acStatus = view.findViewById(R.id.ac_status)
        lightSection = view.findViewById(R.id.light_section)
        lightStatus = view.findViewById(R.id.light_status)
        doorSection = view.findViewById(R.id.door_section)
        doorStatus = view.findViewById(R.id.door_status)
        gallerySection = view.findViewById(R.id.gallery_section)

        initDeviceData()
        connectMQTT()

        return view
    }

    private fun onClickDeviceSection(section: LinearLayout, device: String, status: Boolean) {
        section.setOnClickListener {
            showPowerDialog(device, status)
        }
    }

    private fun initDeviceData() {
        Log.d("initDeviceData", "initDeviceData")
        RetrofitInstance.apiServiceInterface.getAllDevices(rcvRoom)
            .enqueue(object : Callback<List<DeviceData>> {
                override fun onResponse(
                    call: Call<List<DeviceData>>,
                    response: Response<List<DeviceData>>
                ) {
                    if (response.body() == null) {
                        Log.d("Error List Device: ", "Error")
                        return
                    }
                    val result = response.body()!!

                    for (device in result) {
                        device.topic?.let { topicList.add(it) }
                        for (function in device.function) {
                            deviceList.add(function)
                        }
                    }

                    initDeviceList()
                    getSubscribeTopicList()
                    getPublishTopic()
                }

                override fun onFailure(call: Call<List<DeviceData>>, t: Throwable) {
                    Log.d("Error Device List: ", "Error")
                }
            })
    }

    private fun getSubscribeTopicList(){
        var result: MutableList<String> = mutableListOf()
        for (topic in topicList) {
            result.add(topic.subscribe!!)
        }

        for (topic in result) {
            if (topic.contains("CameraPack")) {
                if (deviceList.contains("LightState")) {
                    lightSubscribeTopic = topic
                }
                if (deviceList.contains("AC")) {
                    acSubscribeTopic = topic
                }
            }
            if (topic.contains("Door")) {
                doorSubscribeTopic = topic
            }
        }
    }

    private fun getPublishTopic(){
        var result: MutableList<String> = mutableListOf()
        for (topic in topicList) {
            result.add(topic.publish!!)
        }

        for (topic in result) {
            if (topic.contains("CameraPack")) {
                if (deviceList.contains("LightState")) {
                    lightPublishTopic = topic
                }
                if (deviceList.contains("AC")) {
                    acPublishTopic = topic
                }
            }
            if (topic.contains("Door")) {
                doorPublishTopic = topic
            }
        }

    }

    private fun initDeviceList() {
        if (deviceList.contains("Door")) {
            doorSection.visibility = View.VISIBLE
            countDevice++
        }

        if (deviceList.contains("AC")) {
            acSection.visibility = View.VISIBLE
            onClickDeviceSection(acSection, "AC", true)
            countDevice++
        }

        if (deviceList.contains("LightState")) {
            onClickDeviceSection(lightSection, "Light", false)
            lightSection.visibility = View.VISIBLE
            countDevice++
        }

        if (haveYolo) {
            gallerySection.visibility = View.VISIBLE
            gallerySection.setOnClickListener {
                val intent = Intent(context, GalleryActivity::class.java)
                // start your next activity
                startActivity(intent)
            }
        }
    }

    private fun showPowerDialog(title: String, status: Boolean) {
        val dialogView = View.inflate(context, R.layout.power_dialog, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        val powerDialog = builder.create()
        powerDialog.show()
        powerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var powerDialogTitle = dialogView.findViewById<TextView>(R.id.power_dialog_title)
        var powerDialogCurrentStatus = dialogView.findViewById<TextView>(R.id.status_text)
        var powerDialogNextStatus = dialogView.findViewById<TextView>(R.id.question_text)

        powerDialogTitle.text = title
        if (status) {
            powerDialogCurrentStatus.text = "ON"
            powerDialogNextStatus.text = "OFF"
        } else {
            powerDialogCurrentStatus.text = "OFF"
            powerDialogNextStatus.text = "ON"
        }

        var powerDialogCancelButton = dialogView.findViewById<Button>(R.id.cancel_btn)
        powerDialogCancelButton.setOnClickListener {
            powerDialog.dismiss()
        }
        var powerDialogOkButton = dialogView.findViewById<Button>(R.id.ok_btn)

    }

    private fun showAlertDialog() {
        val dialogView = View.inflate(context, R.layout.alert_dialog, null)
        val builder = android.app.AlertDialog.Builder(context)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var okBtn = dialogView.findViewById<Button>(R.id.ok_btn)
        var okSection = dialogView.findViewById<CardView>(R.id.ok_section)

        okBtn.setOnClickListener{
            disconnectMQTT()
            alertDialog.dismiss()
        }
        okSection.setOnClickListener{
            disconnectMQTT()
            alertDialog.dismiss()
        }

        var alertText = dialogView.findViewById<TextView>(R.id.option_dialog_text)
        alertText.text = "There is an error occurred. Please restart and try again"
    }

    private fun connectMQTT() {
        mqttClient = MQTTClient(context, BROKER, CLIENT_ID)
        mqttClient.connect(USERNAME_MQTT, PASSWORD_MQTT, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d(this.javaClass.name, "Connection success")
                subscribeDeviceTopic()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d(this.javaClass.name, "Connection failure: ${exception.toString()}")
                showAlertDialog()
            }
        },
        object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                Log.d(this.javaClass.name, "Connection lost ${cause.toString()}")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val msg = "Receive message: ${message.toString()} from topic: $topic"
                Log.d(this.javaClass.name, msg)

                if (message.toString().contains("Temp")) {
                    acStatus.visibility = View.VISIBLE
                    val msg = message.toString().replace("Temp: ", "")
                    if (msg == "1") {
                        acStatus.setImageResource(R.drawable.off_icon)
                        onClickDeviceSection(acSection, "AC", false)
                    } else {
                        acStatus.setImageResource(R.drawable.on_icon)
                        onClickDeviceSection(acSection, "DoACor", true)
                    }
                } else if (message.toString().contains("Light")) {
                    lightStatus.visibility = View.VISIBLE
                    val msg = message.toString().replace("Light: ", "")
                    if (msg == "1") {
                        lightStatus.setImageResource(R.drawable.off_icon)
                        onClickDeviceSection(lightSection, "Light", false)
                    } else {
                        lightStatus.setImageResource(R.drawable.on_icon)
                        onClickDeviceSection(lightSection, "Light", true)
                    }
                } else {
                    doorStatus.visibility = View.VISIBLE
                    if (message.toString() == "1") {
//                        doorStatus.setImageResource(R.drawable.unlocked_ic)
                        doorStatus.setImageResource(R.drawable.locked_ic)
                        context?.let { ContextCompat.getColor(it, R.color.tv_color) }
                            ?.let { doorStatus.setColorFilter(it, android.graphics.PorterDuff.Mode.SRC_IN) }
                        onClickDeviceSection(doorSection, "Door", false)

                    } else  {
                        doorStatus.setImageResource(R.drawable.locked_ic)
                        onClickDeviceSection(doorSection, "Door", true)
                    }
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d(this.javaClass.name, "Delivery complete")
            }

        })
    }

    private fun subscribeTopic(topic: String) {
        if (mqttClient.isConnected()) {
            mqttClient.subscribe(topic, 0, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    val msg = "Subscribed to: $topic"
                    Log.d(this.javaClass.name, msg)

                    if (topic == acPublishTopic && !isSubscribeAc) {
                        publishTopic(acSubscribeTopic, REQUEST_AC_STATE)
                        isSubscribeAc = true
                    }
                    if (topic == lightPublishTopic && !isSubscribeLight) {
                        publishTopic(lightSubscribeTopic, REQUEST_LIGHT_STATE)
                        isSubscribeLight = true
                    }
                    if (topic == doorPublishTopic) {
                        publishTopic(doorSubscribeTopic, REQUEST_DOOR)
                    }
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken?,
                    exception: Throwable?
                ) {
                    Log.d(this.javaClass.name, "Failed to subscribe: $topic")
                    showAlertDialog()
                }
            })
        } else {
            Log.d(this.javaClass.name, "Failed to subscribe")
        }
    }

    // subscribe topic Publish
    private fun subscribeDeviceTopic() {
        Log.d("in here", "subscribe")
        if (acPublishTopic.isNotEmpty() && !isPublishAc) {
            subscribeTopic(acPublishTopic)
            isPublishAc = true
        }
        if (lightPublishTopic.isNotEmpty() && !isPublishLight) {
            subscribeTopic(lightPublishTopic)
            isPublishLight = true
        }
        if (doorPublishTopic.isNotEmpty() && !isPublishDoor) {
            subscribeTopic(doorPublishTopic)
            isPublishDoor = true
        }
        Handler().postDelayed({
            if (!isPublishAc && !isPublishLight && !isPublishDoor) {
                showAlertDialog()
            }
        }, 5000)
    }

    private fun publishTopic(topic: String, request: String) {
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

    private fun disconnectMQTT() {
        if (mqttClient.isConnected()) {
            // Disconnect from MQTT Broker
            mqttClient.disconnect(object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(this.javaClass.name, "Disconnected")
                    var intent: Intent = Intent(context, RoomListActivity::class.java)
                    startActivity(intent)
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken?,
                    exception: Throwable?
                ) {
                    Log.d(this.javaClass.name, "Failed to disconnect")
                    showAlertDialog()
                }
            })
        } else {
            Log.d(this.javaClass.name, "Impossible to disconnect, no server connected")
            showAlertDialog()
        }
    }
}