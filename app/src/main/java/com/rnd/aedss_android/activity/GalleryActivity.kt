package com.rnd.aedss_android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rnd.aedss_android.R
import com.rnd.aedss_android.adapter.GalleryAdapter
import com.rnd.aedss_android.datamodel.ImageData
import com.rnd.aedss_android.datamodel.YoloData
import com.rnd.aedss_android.utils.Constants
import com.rnd.aedss_android.utils.api.RetrofitInstance
import com.rnd.aedss_android.utils.mqtt.MQTTClient
import com.rnd.aedss_android.utils.preferences.AuthenticationPreferences
import com.rnd.aedss_android.utils.preferences.RoomPreferences
import com.rnd.aedss_android.viewmodel.SectionImage
import org.eclipse.paho.client.mqttv3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GalleryActivity : AppCompatActivity() {

    private lateinit var sectionListRcv: RecyclerView
    var sectionImageList: MutableList<SectionImage> = mutableListOf()

    lateinit var roomSession: RoomPreferences
    lateinit var rcvRoom: String

    lateinit var authSession: AuthenticationPreferences
    var auth: String = ""
    var userid: String = ""

    var topicPublish: String = ""
    var topicSubscribe: String = ""
    var requestPackage: String = ""

    val detailList = """
        [{"date": "2023-04-03", "ids": ["1k1h88DV6s6wZ1HwKKsltYgGvVDc_mLcT", "1BexwF2hTzYrSnOhi5kfsQFuTYXVbxgeP", "12EX3FgqKLCCMJo707Wf6tyoeKfbBUogc"]}, {"date": "2023-03-27", "ids": ["1TcRxybSmyANW8kbpQzzS71RhkhFBUrKs", "1ICho0YbJHsC6eD-M-mTFnkZTM4HL5cXL"]}]
    """

    private lateinit var mqttClient: MQTTClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        roomSession = RoomPreferences(this)
        rcvRoom = roomSession.getRoomName().toString()

        authSession = AuthenticationPreferences(this)
        auth = authSession.getAuthToken().toString()
        userid = authSession.getUserid().toString()

        var id: String =
            Constants.CLIENT_ID + "_" + (('a'..'z').random()).toString() + "_" + ((0..1000).random()).toString()
        mqttClient = MQTTClient(this, Constants.BROKER, id)

        initYoloData()

        sectionListRcv = findViewById(R.id.gallery_list)
        sectionListRcv.setHasFixedSize(true)
        sectionListRcv.layoutManager = LinearLayoutManager(this)
        sectionListRcv.adapter = GalleryAdapter(applicationContext, sectionImageList)
//        showGallery(detailList)
    }

    private fun initYoloData() {
        RetrofitInstance.apiServiceInterface.getYoloDetail(auth, userid, rcvRoom)
            .enqueue(object : Callback<List<YoloData>> {
                override fun onResponse(
                    call: Call<List<YoloData>>,
                    response: Response<List<YoloData>>
                ) {
                    if (response?.body() == null) {
                        showAlertDialog()
                        Log.d("Error yolo: ", "Error")
                    } else {
                        disconnectMQTT()
                        var result = response.body()!!
                        for (item in result) {
                            if (item.publish != null) {
                                topicPublish = item.publish!!
                            }
                            if (item.subscribe != null) {
                                topicSubscribe = item.subscribe!!
                            }
                            for (request in item.request) {
                                if (request.contains("serverRequestID")) {
                                    requestPackage = request
                                    break
                                }
                            }
                        }
                        connectMQTT()
                    }
                }

                override fun onFailure(call: Call<List<YoloData>>, t: Throwable) {
                    Log.d("Error yolo: ", "Error")
                }

            })
    }

    private fun showAlertDialog() {
        val dialogView = View.inflate(this, R.layout.alert_dialog, null)
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
            alertDialog.dismiss()
            var intent: Intent = Intent(applicationContext, GalleryActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        okBtn.setOnClickListener {
            alertDialog.dismiss()
            var intent: Intent = Intent(applicationContext, GalleryActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun disconnectMQTT() {
        if (mqttClient.isConnected()) {
            // Disconnect from MQTT Broker
            mqttClient.disconnect(object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(this.javaClass.name, "Disconnected")
                    var intent: Intent = Intent(applicationContext, RoomListActivity::class.java)
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
        }
    }

    private fun connectMQTT() {
        mqttClient.connect(
            Constants.USERNAME_MQTT, Constants.PASSWORD_MQTT, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(this.javaClass.name, "Connection success")
                    subscribeTopic(topicPublish)
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
                    Log.d(this.javaClass.name, message.toString())
                    showGallery(message.toString())
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d(this.javaClass.name, "Delivery complete")
                }

            })
    }

    private fun publishTopic() {
        mqttClient.publish(topicSubscribe, requestPackage, 0, false, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                val msg = "Publish message: $requestPackage to topic $topicSubscribe"
                Log.d(this.javaClass.name, msg)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d(this.javaClass.name, "Failed to publish message to topic")
            }

        })
    }

    private fun subscribeTopic(topic: String) {
        if (mqttClient.isConnected()) {
            mqttClient.subscribe(topic, 0, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    val msg = "Subscribed to: $topic"
                    Log.d(this.javaClass.name, msg)
                    publishTopic()
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

    private fun showGallery(receiveMessage: String) {
        val typeToken = object : TypeToken<List<ImageData>>() {}.type
        val list = Gson().fromJson<List<ImageData>>(receiveMessage, typeToken)

        for (item in list) {
            var sectionImage = item.date?.let { SectionImage(it, item.ids.toString()) }
            if (sectionImage != null) {
                sectionImageList.add(sectionImage)
            }
        }

        sectionListRcv.adapter?.notifyDataSetChanged()
    }
}