package com.rnd.aedss_android.utils.mqtt

import android.content.Context
import android.util.Log
import com.rnd.aedss_android.datamodel.device_data.MQTTConnection
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.util.*

class MQTTManager(val mqttConnection: MQTTConnection, val context:Context) {
    private var client = MqttAndroidClient(context,mqttConnection.broker,mqttConnection.clientID + id(context))
    private var uniqueID:String? = null
    private val PREF_UNIQUE_ID = "PREF_UNIQUE_ID"

    init {
        client.setCallback(object: MqttCallbackExtended {
            override fun connectComplete(b:Boolean, address:String) {
                Log.d("mqttStatus", "connect")
            }
            override fun connectionLost(throwable:Throwable) {
                Log.d("mqttStatus", "disconnect")
            }
            override fun messageArrived(topic:String, mqttMessage: MqttMessage) {
                Log.d("Mqtt messageArrived", mqttMessage.toString())
            }
            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                Log.d("Mqtt deliveryComplete", "success")
            }
        })
    }

    fun connect(){
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false
        mqttConnectOptions.userName = this.mqttConnection.username
        mqttConnectOptions.password = this.mqttConnection.password.toCharArray()
        try
        {
            var params = this.mqttConnection
            client.connect(mqttConnectOptions, null, object: IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    client.setBufferOpts(disconnectedBufferOptions)
                    subscribe(params.topic)

                }
                override fun onFailure(asyncActionToken: IMqttToken, exception:Throwable) {
                    Log.d("Mqtt fail connect", "Failed to connect to: " + params.broker + exception.toString())
                }
            })
        }
        catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    @Synchronized fun id(context:Context):String {
        if (uniqueID == null)
        {
            val sharedPrefs = context.getSharedPreferences(
                PREF_UNIQUE_ID, Context.MODE_PRIVATE)
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null)
            if (uniqueID == null)
            {
                uniqueID = UUID.randomUUID().toString()
                val editor = sharedPrefs.edit()
                editor.putString(PREF_UNIQUE_ID, uniqueID)
                editor.commit()
            }
        }
        return uniqueID!!
    }

    // Subscribe to topic
    fun subscribe(topic: String){
        try
        {
            client.subscribe(topic, 0, null, object:IMqttActionListener {
                override fun onSuccess(asyncActionToken:IMqttToken) {
                    Log.d("Mqtt subscribeStatus", "Subscription!")
                }
                override fun onFailure(asyncActionToken:IMqttToken, exception:Throwable) {
                    Log.d("Mqtt subscribeStatus", "Subscription fail!")
                }
            })
        }
        catch (ex:MqttException) {
            System.err.println("Exception subscribing")
            ex.printStackTrace()
        }
    }

    fun publish(message:String){
        try
        {
            client.publish(this.mqttConnection.topic,message.toByteArray(),0,false,null,object :IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("Mqtt publishStatus", "Publish Success!")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("Mqtt publishStatus", "Publish Failed!")
                }

            })
        }
        catch (ex:MqttException) {
            System.err.println("Exception publishing")
            ex.printStackTrace()
        }
    }
}