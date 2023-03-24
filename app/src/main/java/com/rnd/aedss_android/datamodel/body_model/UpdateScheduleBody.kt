package com.rnd.aedss_android.datamodel.body_model

data class UpdateScheduleBody(
    var userID: String? = null,
    var timeOn: String? = null,
    var timeOff: String? = null,
    var repeat: String? = null,
    var dayOfTheWeek: String? = null,
    var request: String? = null
)
