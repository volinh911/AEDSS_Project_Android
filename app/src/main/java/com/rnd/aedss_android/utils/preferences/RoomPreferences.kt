package com.rnd.aedss_android.utils.preferences

import android.content.Context
import android.content.SharedPreferences
import com.rnd.aedss_android.utils.Constants.Companion.DEVICE_LIST
import com.rnd.aedss_android.utils.Constants.Companion.HAVE_YOLO
import com.rnd.aedss_android.utils.Constants.Companion.PUBLISH_TOPIC_YOLO
import com.rnd.aedss_android.utils.Constants.Companion.ROOM_NAME
import com.rnd.aedss_android.utils.Constants.Companion.ROOM_PREF
import com.rnd.aedss_android.utils.Constants.Companion.SUBSCRIBE_TOPIC_YOLO

class RoomPreferences {
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor
    var context: Context
    var PRIVATE_MODE: Int = 0

    constructor(context: Context) {
        this.context = context
        pref = context.getSharedPreferences(ROOM_PREF, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun addRoomSession(roomName: String) {
        editor.putString(ROOM_NAME, roomName)
        editor.commit()
    }

    fun addYoloSession(bool: Boolean) {
        editor.putBoolean(HAVE_YOLO, bool)
        editor.commit()
    }

    fun getRoomName(): String? {
        return pref.getString(ROOM_NAME, "")
    }

    fun doesHaveYolo(): Boolean? {
        return pref.getBoolean(HAVE_YOLO, false)
    }

    fun addDeviceList(deviceList: String) {
        editor.putString(DEVICE_LIST, deviceList)
        editor.commit()
    }

    fun getDeviceList(): String? {
        return pref.getString(DEVICE_LIST, "")
    }
}