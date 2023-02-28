package com.rnd.aedss_android.datamodel.device_data

data class RequestData(
    var door : String? = null,
    var pir  : String? = null,
    var api  : String? = null,
    var acOn  : String? = null,
    var acOff  : String? = null,
    var lightOn  : String? = null,
    var lightOff  : String? = null,
    var temp  : String? = null,
    var lightState  : String? = null,
    var servo  : String? = null,
)
