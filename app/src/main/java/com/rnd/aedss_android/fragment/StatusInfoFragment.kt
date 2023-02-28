package com.rnd.aedss_android.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.rnd.aedss_android.R
import com.rnd.aedss_android.activity.GalleryActivity
import com.rnd.aedss_android.datamodel.device_data.*
import com.rnd.aedss_android.utils.Constants.Companion.BROKER
import com.rnd.aedss_android.utils.Constants.Companion.CLIENT_ID
import com.rnd.aedss_android.utils.mqtt.MQTTManager
import com.rnd.aedss_android.utils.api.RetrofitInstance
import com.rnd.aedss_android.utils.preferences.RoomPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    var ackList: MutableList<AckData> = mutableListOf()
    var requestList: MutableList<RequestData> = mutableListOf()
    var deviceList: MutableList<String> = mutableListOf() //function in api
    var topicList: MutableList<TopicData> = mutableListOf()

    var mqttManager: MQTTManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_status_info, container, false)

        roomSession = context?.let { RoomPreferences(it) }!!
        rcvRoom = roomSession.getRoomName()!!
        haveYolo = roomSession.doesHaveYolo()!!

        initDeviceData()

        acSection = view.findViewById(R.id.ac_section)
        acStatus = view.findViewById(R.id.ac_status)
        lightSection = view.findViewById(R.id.light_section)
        lightStatus = view.findViewById(R.id.light_status)
        doorSection = view.findViewById(R.id.door_section)
        doorStatus = view.findViewById(R.id.door_status)
        gallerySection = view.findViewById(R.id.gallery_section)

        return view
    }

    private fun onClickDeviceSection(section: LinearLayout, device: String, status: Boolean) {
        section.setOnClickListener {
            showPowerDialog(device, status)
        }
    }

    private fun initDeviceData() {
        RetrofitInstance.apiServiceInterface.getAllDevices(rcvRoom)
            .enqueue(object : Callback<List<DeviceData>> {
                override fun onResponse(
                    call: Call<List<DeviceData>>,
                    response: Response<List<DeviceData>>
                ) {
                    if (response?.body() == null) {
                        Log.d("Error List Device: ", "Error")
                        return
                    }
                    var result = response.body()!!

                    for (device in result) {
                        device.ack?.let { ackList.add(it) }

                        device.request?.let { requestList.add(it) }

                        device.topic?.let { topicList.add(it) }
                        for (function in device.function) {
                            deviceList.add(function)
                        }
                    }

                    initDeviceList()
                }

                override fun onFailure(call: Call<List<DeviceData>>, t: Throwable) {
                    Log.d("Error Device List: ", "Error")
                }
            })
    }

    private fun getFinalRequestList(): MutableList<String> {
        var result: MutableList<String> = mutableListOf()
        for (request in requestList) {
            if (request.door != null) {
                result.add(request.door!!)
            }
            if (request.acOn != null) {
                result.add(request.acOn!!)
            }
            if (request.acOff != null) {
                result.add(request.acOff!!)
            }
            if (request.lightOn != null) {
                result.add(request.lightOn!!)
            }
            if (request.lightOff != null) {
                result.add(request.lightOff!!)
            }
            if (request.temp != null) {
                result.add(request.temp!!)
            }
            if (request.lightState != null) {
                result.add(request.lightState!!)
            }
        }
        return result
    }

    private fun getSubscribeTopicList(): MutableList<String> {
        var result: MutableList<String> = mutableListOf()
        for (topic in topicList) {
            result.add(topic.subscribe!!)
        }
        return result
    }

    private fun getPublishTopicList(): MutableList<String> {
        var result: MutableList<String> = mutableListOf()
        for (topic in topicList) {
            result.add(topic.publish!!)
        }
        return result
    }

    private fun connectMQTT(topic: String) {
        var mqttConnection = MQTTConnection(CLIENT_ID, BROKER, topic, "", "")
        mqttManager = context?.let { MQTTManager(mqttConnection, it) }
        mqttManager?.connect()
    }

    private fun getLightStatus() {

    }

    private fun getACStatus() {

    }

    private fun getDoorStatus() {

    }

    private fun initDeviceList() {
        if (deviceList.contains("Door")) {
            doorSection.visibility = View.VISIBLE
            onClickDeviceSection(doorSection, "Door", false)
        }

        if (deviceList.contains("AC")) {
            acSection.visibility = View.VISIBLE
            onClickDeviceSection(acSection, "AC", true)
        }

        if (deviceList.contains("LightState")) {
            onClickDeviceSection(lightSection, "Light", false)
            lightSection.visibility = View.VISIBLE
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
}