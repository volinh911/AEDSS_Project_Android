package com.rnd.aedss_android.datamodel.device_data

data class DeviceData(
    var _id: String? = null,
    var deviceName: String? = null,
    var room: String? = null,
    var deviceModule: String? = null,
    var function: ArrayList<String> = arrayListOf(),
    var topic: TopicData? = null,
    var ack: AckData? = null,
    var request: RequestData? = null,
    var pin: PinData? = null
)
