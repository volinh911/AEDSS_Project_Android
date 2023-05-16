package com.rnd.aedss_android.datamodel.body_model

data class PostScheduleBody(
    var userID: String? = null,
    var deviceName: String? = null,
    var deviceModule: String? = null,
    var room: String? = null,
    var timeOn: String? = null,
    var timeOff: String? = null,
    var rerun: String? = null,
    var dayOfTheWeek: String? = null,
    var request: String? = null
)
