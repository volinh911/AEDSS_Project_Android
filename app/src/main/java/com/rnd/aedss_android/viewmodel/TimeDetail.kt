package com.rnd.aedss_android.viewmodel

data class TimeDetail(
    val scheduleId: String,
    val day: String,
    val device: String,
    val repeat: Boolean,
    val from: String,
    val to: String
) {}
