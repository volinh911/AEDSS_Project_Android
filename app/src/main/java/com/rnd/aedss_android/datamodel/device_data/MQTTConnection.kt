package com.rnd.aedss_android.datamodel.device_data

data class MQTTConnection(
    val clientID: String,
    val broker: String,
    val topic: String,
    val username: String,
    val password: String
)
