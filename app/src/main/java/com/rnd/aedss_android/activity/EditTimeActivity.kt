package com.rnd.aedss_android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.rnd.aedss_android.utils.Constants
import com.rnd.aedss_android.R
import com.rnd.aedss_android.datamodel.ResponseData
import com.rnd.aedss_android.datamodel.body_model.PostScheduleBody
import com.rnd.aedss_android.datamodel.body_model.UpdateScheduleBody
import com.rnd.aedss_android.utils.Constants.Companion.AC_DEVICE
import com.rnd.aedss_android.utils.Constants.Companion.AC_DEVICE_FULL
import com.rnd.aedss_android.utils.Constants.Companion.DAY_LIST
import com.rnd.aedss_android.utils.Constants.Companion.LIGHT_DEVICE
import com.rnd.aedss_android.utils.Constants.Companion.RCV_SCHEDULE_DAY
import com.rnd.aedss_android.utils.Constants.Companion.RCV_SCHEDULE_DEVICE
import com.rnd.aedss_android.utils.Constants.Companion.RCV_SCHEDULE_FROM
import com.rnd.aedss_android.utils.Constants.Companion.REQUEST_AC_OFF
import com.rnd.aedss_android.utils.Constants.Companion.REQUEST_AC_ON
import com.rnd.aedss_android.utils.Constants.Companion.REQUEST_LIGHT_OFF
import com.rnd.aedss_android.utils.Constants.Companion.REQUEST_LIGHT_ON
import com.rnd.aedss_android.utils.Constants.Companion.SCHEDULE
import com.rnd.aedss_android.utils.Constants.Companion.RCV_SCHEDULE_ID
import com.rnd.aedss_android.utils.Constants.Companion.RCV_SCHEDULE_REPEAT
import com.rnd.aedss_android.utils.Constants.Companion.RCV_SCHEDULE_TO
import com.rnd.aedss_android.utils.Constants.Companion.createHourList
import com.rnd.aedss_android.utils.Constants.Companion.createMinList
import com.rnd.aedss_android.utils.api.RetrofitInstance
import com.rnd.aedss_android.utils.mqtt.MQTTClient
import com.rnd.aedss_android.utils.preferences.AuthenticationPreferences
import com.rnd.aedss_android.utils.preferences.RoomPreferences
import org.eclipse.paho.client.mqttv3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditTimeActivity : AppCompatActivity() {

    private lateinit var daySpinner: Spinner

    private lateinit var hourFromSpinner: Spinner
    private lateinit var minFromSpinner: Spinner
    private lateinit var periodFromSpinner: Spinner

    private lateinit var hourToSpinner: Spinner
    private lateinit var minToSpinner: Spinner

    //    private lateinit var periodToSpinner: Spinner
    private lateinit var submitPostBtn: Button
    private lateinit var submitUpdateBtn: Button

    private lateinit var titleText: TextView

    private lateinit var deviceOption: RadioGroup
    private lateinit var acOption: RadioButton
    private lateinit var lightOption: RadioButton

    private lateinit var fromOption: CheckBox
    private lateinit var toOption: CheckBox
    private lateinit var repeatOption: CheckBox

    var rcvValue: Int = 0

    lateinit var roomSession: RoomPreferences
    var rcvRoom: String = ""
    var rcvDeviceLst: String = ""

    lateinit var authSession: AuthenticationPreferences
    var auth: String = ""
    var userid: String = ""

    var rcvScheduleId: String = ""
    var rcvScheduleDay: String = ""
    var rcvScheduleDevice: String = ""
    var rcvScheduleRepeat: Boolean = false
    var rcvScheduleFrom: String = ""
    var rcvScheduleTo: String = ""
    private lateinit var mqttClient: MQTTClient

    val POST_SCHEDULE_ALERT = "post response"
    val EDIT_SCHEDULE_ALERT = "edit response"
    val ALERT = " alert"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_time)

        roomSession = RoomPreferences(this)
        rcvRoom = roomSession.getRoomName().toString()
        rcvDeviceLst = roomSession.getDeviceList().toString()

        authSession = AuthenticationPreferences(this)
        auth = authSession.getAuthToken().toString()
        userid = authSession.getUserid().toString()

        var id: String = Constants.CLIENT_ID + "_" + (('a'..'z').random()).toString() + "_" + ((0..1000).random()).toString()
        mqttClient = MQTTClient(this, Constants.BROKER, id)

        daySpinner = findViewById(R.id.day_spinner)
        val daySpinnerAdapter =
            ArrayAdapter<String>(this, R.layout.day_spinner_item, Constants.DAY_LIST)
        daySpinner.adapter = daySpinnerAdapter

        hourFromSpinner = findViewById(R.id.hour_spinner_from)
        val hourFromSpinnerAdapter =
            ArrayAdapter<String>(this, R.layout.day_spinner_item, Constants.createHourList())
        hourFromSpinner.adapter = hourFromSpinnerAdapter

        minFromSpinner = findViewById(R.id.min_spinner_from)
        val minFromSpinnerAdapter =
            ArrayAdapter<String>(this, R.layout.day_spinner_item, Constants.createMinList())
        minFromSpinner.adapter = minFromSpinnerAdapter

        periodFromSpinner = findViewById(R.id.period_spinner_from)
        val periodFromSpinnerAdapter =
            ArrayAdapter<String>(this, R.layout.day_spinner_item, Constants.PERIOD_LIST)
        periodFromSpinner.adapter = periodFromSpinnerAdapter

        hourToSpinner = findViewById(R.id.hour_spinner_to)
        val hourToSpinnerAdapter =
            ArrayAdapter<String>(this, R.layout.day_spinner_item, Constants.createHourList())
        hourToSpinner.adapter = hourToSpinnerAdapter

        minToSpinner = findViewById(R.id.min_spinner_to)
        val minToSpinnerAdapter =
            ArrayAdapter<String>(this, R.layout.day_spinner_item, Constants.createMinList())
        minToSpinner.adapter = minToSpinnerAdapter

        fromOption = findViewById(R.id.from_option)
        toOption = findViewById(R.id.to_option)
        repeatOption = findViewById(R.id.repeat_option)
        lightOption = findViewById(R.id.light_option)
        acOption = findViewById(R.id.ac_option)

        deviceOption = findViewById(R.id.device_option)
        submitPostBtn = findViewById(R.id.submit_post_btn)
        submitUpdateBtn = findViewById(R.id.submit_update_btn)

        titleText = findViewById(R.id.title_name)
        var intent = intent
        if (intent != null) {
            rcvValue = intent.getIntExtra(SCHEDULE, 0)
            if (rcvValue == 0) {
                titleText.text = "Add time"
                submitPostBtn.visibility = View.VISIBLE
            } else {
                titleText.text = "Edit time"
                submitUpdateBtn.visibility = View.VISIBLE
                rcvScheduleId = intent.getStringExtra(RCV_SCHEDULE_ID).toString()
                rcvScheduleDay = intent.getStringExtra(RCV_SCHEDULE_DAY).toString()
                rcvScheduleDevice = intent.getStringExtra(RCV_SCHEDULE_DEVICE).toString()
                rcvScheduleRepeat = intent.getBooleanExtra(RCV_SCHEDULE_REPEAT, false)
                rcvScheduleFrom = intent.getStringExtra(RCV_SCHEDULE_FROM).toString()
                rcvScheduleTo = intent.getStringExtra(RCV_SCHEDULE_TO).toString()
                showCurrentScheduleForUpdate()
            }
        }

        if (rcvDeviceLst.contains(AC_DEVICE)) {
            acOption.visibility = View.VISIBLE
        }
        if (rcvDeviceLst.contains(LIGHT_DEVICE)) {
            lightOption.visibility = View.VISIBLE
        }

        submitPostBtn.setOnClickListener {
            disconnectMQTT()
            getBodySchedule()
        }
        submitUpdateBtn.setOnClickListener {
            disconnectMQTT()
            getBodySchedule()
        }

    }

    // create new schedule
    private fun getBodySchedule() {
        var deviceName: String = "ESP"
        var deviceModule: String = "" //chosen device name
        var timeOn: String = ""
        var timeOff: String = ""
        var repeat: String = ""
        var dayOfTheWeek: String = ""
        var request: String = ""

        var chosenDevice: RadioButton? = findViewById(deviceOption.checkedRadioButtonId)
        if (chosenDevice != null) {
            if (chosenDevice.text.contains(AC_DEVICE_FULL)) {
                deviceModule = AC_DEVICE
                if (fromOption.isChecked) {
                    timeOn =
                        hourFromSpinner.selectedItem.toString() + ":" + minFromSpinner.selectedItem.toString()
                    request += REQUEST_AC_ON
                } else {
                    timeOn = "-1"
                }
                if (toOption.isChecked) {
                    timeOff =
                        hourToSpinner.selectedItem.toString() + ":" + minToSpinner.selectedItem.toString()
                    request += if (request.isNotEmpty()) {
                        ",$REQUEST_AC_OFF"
                    } else {
                        REQUEST_AC_OFF
                    }
                } else {
                    timeOff = "-1"
                }
            } else if (chosenDevice.text.contains(LIGHT_DEVICE)) {
                deviceModule = LIGHT_DEVICE
                if (fromOption.isChecked) {
                    timeOn =
                        hourFromSpinner.selectedItem.toString() + ":" + minFromSpinner.selectedItem.toString()
                    request += REQUEST_LIGHT_ON
                } else {
                    timeOn = "-1"
                }
                if (toOption.isChecked) {
                    timeOff =
                        hourToSpinner.selectedItem.toString() + ":" + minToSpinner.selectedItem.toString()
                    request += if (request.isNotEmpty()) {
                        ", $REQUEST_LIGHT_OFF"
                    } else {
                        REQUEST_LIGHT_OFF
                    }
                } else {
                    timeOff = "-1"
                }
            }
        }

        if (daySpinner.selectedItem.toString() != "No Choice") {
            dayOfTheWeek = daySpinner.selectedItem.toString()
            when (dayOfTheWeek) {
                "Monday" -> { dayOfTheWeek = "1" }
                "Tuesday" -> { dayOfTheWeek = "2" }
                "Wednesday" -> { dayOfTheWeek = "3" }
                "Thursday" -> { dayOfTheWeek = "4" }
                "Friday" -> { dayOfTheWeek = "5" }
                "Saturday" -> { dayOfTheWeek = "6" }
                "Sunday" -> { dayOfTheWeek = "0" }
            }
        }

        repeat = if (repeatOption.isChecked) {
            "yes"
        } else {
            "no"
        }

        if (chosenDevice == null
            || (!fromOption.isChecked && !toOption.isChecked)
            || daySpinner.selectedItem.toString() == "No Choice"
        ) {
            showAlertDialog("", false)
        }

        if (rcvValue == 0) {
            var body = PostScheduleBody(
                userid, deviceName, deviceModule, rcvRoom,
                timeOn, timeOff, repeat, dayOfTheWeek, request
            )
            postSChedule(body)
        } else {
            var body = UpdateScheduleBody(userid, timeOn, timeOff, repeat, dayOfTheWeek, request)
            updateSchedule(body)
        }
    }

    private fun postSChedule(body: PostScheduleBody) {
        RetrofitInstance.apiServiceInterface.postSchedule(auth, userid, body)
            .enqueue(object: Callback<ResponseData> {
                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    if (response?.body() == null) {
                        showAlertDialog(POST_SCHEDULE_ALERT, false)
                        Log.d("response body", "null")
                    }

                    var result = response.body()!!
                    if (result.success == false) {
                        showAlertDialog(POST_SCHEDULE_ALERT, false)
                    } else {
                        var request = "schedule:${result.info}-create@$userid"
                        connectMQTT(request)
                        showAlertDialog(POST_SCHEDULE_ALERT, true)
                    }
                }

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    showAlertDialog(POST_SCHEDULE_ALERT, false)
                    Log.d("post config: ", "failure")
                }

            })
    }

    // update schedule
    private fun showCurrentScheduleForUpdate() {
        for (temp in DAY_LIST) {
            if (rcvScheduleDay == temp) {
                daySpinner.setSelection(DAY_LIST.indexOf(temp))
                break
            }
        }

        if (rcvScheduleDevice == AC_DEVICE) {
            deviceOption.check(acOption.id)
        } else if (rcvScheduleDevice == LIGHT_DEVICE) {
            deviceOption.check(lightOption.id)
        }

        repeatOption.isChecked = rcvScheduleRepeat

        val timeFrom: List<String> = rcvScheduleFrom.split(":").map { it -> it.trim() }
        if (rcvScheduleFrom != "-1") {
            fromOption.isChecked = true
            for (hour in createHourList()) {
                if (timeFrom[0] == hour) {
                    hourFromSpinner.setSelection(createHourList().indexOf(hour))
                    break
                }
            }
            for (min in createMinList()) {
                if (timeFrom[1] == min) {
                    minFromSpinner.setSelection(createMinList().indexOf(min))
                    break
                }
            }
        }

        val timeTo: List<String> = rcvScheduleTo.split(":").map { it -> it.trim() }
        Log.d("to", timeTo[0])
        if (rcvScheduleTo != "-1") {
            toOption.isChecked = true
            for (hour in createHourList()) {
                if (timeTo[0] == hour) {
                    hourToSpinner.setSelection(createHourList().indexOf(hour))
                    break
                }
            }
            for (min in createMinList()) {
                if (timeTo[1] == min) {
                    minToSpinner.setSelection(createMinList().indexOf(min))
                    break
                }
            }
        }
    }

    private fun updateSchedule(body: UpdateScheduleBody) {
        RetrofitInstance.apiServiceInterface.updateSchedule(auth, userid, rcvScheduleId, body)
            .enqueue(object: Callback<ResponseData>{
                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    if (response?.body() == null) {
                        showAlertDialog(EDIT_SCHEDULE_ALERT, false)
                        Log.d("response body", "null")
                    }

                    var result = response.body()!!
                    if (result.success == false) {
                        showAlertDialog(EDIT_SCHEDULE_ALERT, false)
                    } else {
                        var request = "schedule:${rcvScheduleId}-update@$userid"
                        connectMQTT(request)
                        showAlertDialog(EDIT_SCHEDULE_ALERT, true)
                    }
                }

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    showAlertDialog(EDIT_SCHEDULE_ALERT, false)
                    Log.d("edit schedule: ", "failure")
                }
            })
    }

    private fun showAlertDialog(command: String, success: Boolean) {
        val dialogView = View.inflate(this, R.layout.alert_dialog, null)
        val builder = android.app.AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var okBtn = dialogView.findViewById<Button>(R.id.ok_btn)
        var okSection = dialogView.findViewById<CardView>(R.id.ok_section)

        var alertText = dialogView.findViewById<TextView>(R.id.option_dialog_text)
        var titleDialog = dialogView.findViewById<TextView>(R.id.title_dialog)

        if (command.isEmpty()) {
            alertText.text = "Missing option. Please choose all options"
        } else if (command == POST_SCHEDULE_ALERT) {
            if (success) {
                titleDialog.text = "Notification"
                alertText.text =
                    "Create new Schedule successfully. Click OK to move back to Schedule List"

            } else {
                alertText.text = "There is an error occurred. Cannot create new Schedule."
            }
        } else if (command == EDIT_SCHEDULE_ALERT) {
            if (success) {
                titleDialog.text = "Notification"
                alertText.text =
                    "Update Schedule successfully. Click OK to move back to Schedule List"
            } else {
                alertText.text = "There is an error occurred. Cannot update Schedule."
            }
        } else if (command == ALERT) {
            alertText.text = "There is an error occurred. Please restart and try again"
        }

        okBtn.setOnClickListener {
            val intent = Intent(this, RoomInfoActivity::class.java)
            intent.putExtra("room_name", rcvRoom)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        okSection.setOnClickListener {
            val intent = Intent(this, RoomInfoActivity::class.java)
            intent.putExtra("room_name", rcvRoom)
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
                }
                override fun onFailure(
                    asyncActionToken: IMqttToken?,
                    exception: Throwable?
                ) {
                    Log.d(this.javaClass.name, "Failed to disconnect")
                    showAlertDialog(ALERT, false)
                }
            })
        } else {
            Log.d(this.javaClass.name, "Impossible to disconnect, no server connected")
        }
    }

    private fun connectMQTT(request: String) {
        mqttClient.connect(
            Constants.USERNAME_MQTT, Constants.PASSWORD_MQTT, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(this.javaClass.name, "Connection success")
                    publishTopic(request)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(this.javaClass.name, "Connection failure: ${exception.toString()}")
                    showAlertDialog(ALERT, false)
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

    private fun publishTopic(request: String) {
        var topic = "scheduler/server"

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