package com.rnd.aedss_android.utils.preferences

import android.content.Context
import android.content.SharedPreferences
import com.rnd.aedss_android.utils.Constants.Companion.HAVE_YOLO
import com.rnd.aedss_android.utils.Constants.Companion.ROOM_NAME
import com.rnd.aedss_android.utils.Constants.Companion.ROOM_PREF

class RoomPreferences {
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor
    var context: Context
    var PRIVATE_MODE : Int = 0

    constructor(context: Context) {
        this.context = context
        pref = context.getSharedPreferences(ROOM_PREF, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun addRoomSession(roomName: String) {
        editor.putString(ROOM_NAME, roomName)
        editor.commit()
    }

    fun addYoloSession() {
        editor.putBoolean(HAVE_YOLO, true)
        editor.commit()
    }

    fun getRoomName(): String? {
        return pref.getString(ROOM_NAME, "")
    }

    fun doesHaveYolo(): Boolean? {
        return pref.getBoolean(HAVE_YOLO, false)
    }
}