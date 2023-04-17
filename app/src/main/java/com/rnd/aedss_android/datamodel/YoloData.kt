package com.rnd.aedss_android.datamodel

data class YoloData(
    var _id: String? = null,
    var subscribe: String? = null,
    var publish: String? = null,
    var room: String? = null,
    var request: ArrayList<String> = arrayListOf()
)
