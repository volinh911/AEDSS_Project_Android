package com.rnd.aedss_android.datamodel

data class SchedulesData(
    var _id           : String? = null,
    var deviceName   : String? = null,
    var deviceModule : String? = null,
    var room         : String? = null,
    var dayOfTheWeek : String? = null,
    var timeOn       : String? = null,
    var timeOff      : String? = null,
    var repeat       : String? = null,
    var request      : String? = null
)
