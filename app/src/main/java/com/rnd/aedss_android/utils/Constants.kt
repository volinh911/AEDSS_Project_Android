package com.rnd.aedss_android.utils

import java.math.BigInteger
import java.security.MessageDigest

public class Constants {
    companion object {
        val DAY_LIST = listOf<String>(
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday",
            "Sunday"
        )
        val PERIOD_LIST = listOf<String>("AM", "PM")

        fun createHourList(): List<String> {
            var hourList = mutableListOf<String>()
            for (i in 0 until 13) {
                if (i < 10) {
                    hourList.add("0$i")
                } else {
                    hourList.add("$i")
                }
            }
            return hourList
        }

        fun createMinList(): List<String> {
            var minList = mutableListOf<String>()
            for (i in 0 until 56 step 5) {
                if (i == 0 || i == 5) {
                    minList.add("0$i")
                } else {
                    minList.add("$i")
                }
            }
            return minList
        }

        fun convertToMd5(input: String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
        }

        //for device
        const val AC_DEVICE = "AC"
        const val LIGHT_DEVICE = "Light"
        const val TURN_OFF = "turn OFF?"
        const val TURN_ON = "turn ON?"
        const val DOOR_LOCKED = "LOCKED?"
        const val DOOR_UNLOCKED = "UNLOCKED?"

        //for schedule
        const val SCHEDULE = "schedule"
        const val ADD_SCHEDULE = 0
        const val EDIT_SCHEDULE = 1

        // for shared preferences
        const val AUTH_PREF = "Authentication Preferences"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val IS_LOGGED_IN = "isLoggedin"
        const val AUTHENTICATION_TOKEN = "auth"
        const val USER_ID = "userid"

        const val ROOM_PREF = "Room Preferences"
        const val ROOM_NAME = "room name"
        const val HAVE_YOLO = "have yolo"
        const val PUBLISH_TOPIC_YOLO = "publish topic yolo"
        const val SUBSCRIBE_TOPIC_YOLO = "subscribe topic yolo"

        //for API
        const val BASE_URL: String = "https://rndaedss.ddns.net/"
        const val POST_LOGIN: String = "api/v1/user" //no need token
        const val GET_ALL_ROOMS: String = "api/v1/room"
        const val GET_ALL_DEVICES: String = "api/v1/room/device/" //{roomName}
        const val GET_ALL_SCHEDULES: String = "api/v1/room/schedules/" //{roomName}
        const val POST_SCHEDULE: String = "api/v1/schedules"
        const val GET_CONFIG: String = "api/v1/config/" //{roomName}
        const val POST_CONFIG: String = "api/v1/config/"
        const val GET_SCHEDULER_DETAIL: String = "api/v1/scheduler"
        const val GET_YOLO_DETAIL: String = "api/v1/yolov5/" //{roomName}

        // for MQTT
        const val BROKER = "tcp://rndaedss.ddns.net:1883"
        const val CLIENT_ID = "Android_MQTT_AEDSS"
        const val USERNAME_MQTT = "aws"
        const val PASSWORD_MQTT = "Rnd_AEDSS2023"
//        const val USERNAME_MQTT = "pi4"
//        const val PASSWORD_MQTT = "f8a41bcba1561a84f10af0d5851ce93b"

        const val REQUEST_DOOR = "requestDoorStatus"
        const val REQUEST_AC_ON = "requestACOn"
        const val REQUEST_AC_OFF = "requestACOff"
        const val REQUEST_LIGHT_ON = "requestLightOn"
        const val REQUEST_LIGHT_OFF = "requestLightOff"
        const val REQUEST_LIGHT_STATE = "requestLightState"
        const val REQUEST_AC_STATE = "requestTemp"
    }
}