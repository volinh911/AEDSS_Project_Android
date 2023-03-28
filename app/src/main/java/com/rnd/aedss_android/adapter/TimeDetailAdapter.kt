package com.rnd.aedss_android.adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.rnd.aedss_android.utils.Constants
import com.rnd.aedss_android.viewmodel.TimeDetail
import com.rnd.aedss_android.R
import com.rnd.aedss_android.activity.EditTimeActivity
import com.rnd.aedss_android.activity.RoomInfoActivity
import com.rnd.aedss_android.datamodel.ResponseData
import com.rnd.aedss_android.utils.Constants.Companion.EDIT_SCHEDULE
import com.rnd.aedss_android.utils.Constants.Companion.RCV_SCHEDULE_DAY
import com.rnd.aedss_android.utils.Constants.Companion.RCV_SCHEDULE_DEVICE
import com.rnd.aedss_android.utils.Constants.Companion.RCV_SCHEDULE_FROM
import com.rnd.aedss_android.utils.Constants.Companion.SCHEDULE
import com.rnd.aedss_android.utils.Constants.Companion.RCV_SCHEDULE_ID
import com.rnd.aedss_android.utils.Constants.Companion.RCV_SCHEDULE_REPEAT
import com.rnd.aedss_android.utils.Constants.Companion.RCV_SCHEDULE_TO
import com.rnd.aedss_android.utils.api.RetrofitInstance
import com.rnd.aedss_android.utils.mqtt.MQTTClient
import com.rnd.aedss_android.utils.preferences.AuthenticationPreferences
import com.rnd.aedss_android.utils.preferences.RoomPreferences
import org.eclipse.paho.client.mqttv3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TimeDetailAdapter(private val context: Context, timeList: List<TimeDetail>) :
    RecyclerView.Adapter<TimeDetailAdapter.TimeDetailViewHoler>() {

    val list: List<TimeDetail> = timeList
    private lateinit var mqttClient: MQTTClient
    lateinit var authSession: AuthenticationPreferences
    var auth: String = ""
    var userid: String = ""
    val ALERT = " alert"
    lateinit var roomSession: RoomPreferences
    var rcvRoom: String = ""

    inner class TimeDetailViewHoler(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayText: TextView = itemView.findViewById(R.id.day)
        val deviceIcon: ImageView = itemView.findViewById(R.id.device_icon)
        val repeatIcon: ImageView = itemView.findViewById(R.id.repeat_icon)
        val fromText: TextView = itemView.findViewById(R.id.from_text)
        val toText: TextView = itemView.findViewById(R.id.to_text)
        val timeItem: LinearLayout = itemView.findViewById(R.id.time_item)
        val fromNoContent: ImageView = itemView.findViewById(R.id.from_no_content)
        val toNoContent: ImageView = itemView.findViewById(R.id.to_no_content)
        val deleteSchedule: ImageView = itemView.findViewById(R.id.delete_schedule)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeDetailViewHoler {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.time_item, parent, false)
        return TimeDetailViewHoler(view)
    }

    override fun onBindViewHolder(holder: TimeDetailViewHoler, position: Int) {
        authSession = AuthenticationPreferences(context)
        auth = authSession.getAuthToken().toString()
        userid = authSession.getUserid().toString()

        roomSession = RoomPreferences(context)
        rcvRoom = roomSession.getRoomName().toString()

        var id: String = Constants.CLIENT_ID + "_" + (('a'..'z').random()).toString() + "_" + ((0..1000).random()).toString()
        mqttClient = MQTTClient(context, Constants.BROKER, id)
        disconnectMQTT()

        val time = list[position]
        var day: String = ""
        when (time.day) {
            "0" -> {
                day = "Sunday"
            }
            "1" -> {
                day = "Monday"
            }
            "2" -> {
                day = "Tuesday"
            }
            "3" -> {
                day = "Wednesday"
            }
            "4" -> {
                day = "Thursday"
            }
            "5" -> {
                day = "Friday"
            }
            "6" -> {
                day = "Saturday"
            }
            "7" -> {
                day = "Sunday"
            }
        }

        holder.dayText.text = day

        if (time.device == Constants.AC_DEVICE) {
            holder.deviceIcon.setImageResource(R.drawable.ac_icon)
        } else if (time.device == Constants.LIGHT_DEVICE) {
            holder.deviceIcon.setImageResource(R.drawable.light_icon)
        }

        if (time.repeat) {
            holder.repeatIcon.setImageResource(R.drawable.repeat_ic)

        } else {
            holder.repeatIcon.setImageResource(R.drawable.nonrepeat_ic)
        }

        if (time.from == "-1") {
            holder.fromText.visibility = View.GONE
            holder.fromNoContent.visibility = View.VISIBLE
        } else {
            holder.fromText.text = time.from
        }

        if (time.to == "-1") {
            holder.toText.visibility = View.GONE
            holder.toNoContent.visibility = View.VISIBLE
        } else {
            holder.toText.text = time.to
        }

        holder.timeItem.setOnClickListener {
            val intent = Intent(context, EditTimeActivity::class.java)
            intent.putExtra(SCHEDULE, EDIT_SCHEDULE)
            intent.putExtra(RCV_SCHEDULE_ID, time.scheduleId)
            intent.putExtra(RCV_SCHEDULE_DAY, day)
            intent.putExtra(RCV_SCHEDULE_DEVICE, time.device)
            intent.putExtra(RCV_SCHEDULE_REPEAT, time.repeat)
            intent.putExtra(RCV_SCHEDULE_FROM, time.from)
            intent.putExtra(RCV_SCHEDULE_TO, time.to)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        holder.deleteSchedule.setOnClickListener {
            deleteSchedule(time.scheduleId)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun deleteSchedule(scheduleId: String) {
        RetrofitInstance.apiServiceInterface.deleteSchedule(auth, userid, scheduleId)
            .enqueue(object : Callback<ResponseData> {
                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    if (response?.body() == null) {
                        showAlertDialog(ALERT)
                        Log.d("response body", "null")
                    }

                    var result = response.body()!!
                    if (result.success == false) {
                        showAlertDialog(ALERT)
                    } else {
                        var request = "schedule:${scheduleId}-delete@$userid"
                        connectMQTT(request)
                        showAlertDialog("")
                    }
                }

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    showAlertDialog(ALERT)
                    Log.d("delete schedule: ", "failure")
                }

            })
    }

    private fun showAlertDialog(command: String) {
        val dialogView = View.inflate(context, R.layout.alert_dialog, null)
        val builder = android.app.AlertDialog.Builder(context)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var okBtn = dialogView.findViewById<Button>(R.id.ok_btn)
        var okSection = dialogView.findViewById<CardView>(R.id.ok_section)

        var alertText = dialogView.findViewById<TextView>(R.id.option_dialog_text)
        var titleDialog = dialogView.findViewById<TextView>(R.id.title_dialog)

        if (command == ALERT) {
            alertText.text = "There is an error occurred. Please restart and try again"
        } else {
            titleDialog.text = "Notification"
            alertText.text =
                "Delete Schedule successfully. Click OK to move back to Schedule List"
        }

        okBtn.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent(context, RoomInfoActivity::class.java)
            intent.putExtra("room_name", rcvRoom)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
        okSection.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent(context, RoomInfoActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
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
                    showAlertDialog(ALERT)
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
                }
            })
        } else {
            Log.d(this.javaClass.name, "Impossible to disconnect, no server connected")
        }
    }
}