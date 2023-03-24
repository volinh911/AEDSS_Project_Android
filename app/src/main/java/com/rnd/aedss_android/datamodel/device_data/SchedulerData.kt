package com.rnd.aedss_android.datamodel.device_data

data class SchedulerData(
    var _id: String? = null,
    var subscribe: String? = null,
    var publish: String? = null,
    var notify: ArrayList<String> = arrayListOf()
)
