package com.rnd.aedss_android.utils

import java.math.BigInteger
import java.security.MessageDigest

public class Constants {
    companion object {
        val DAY_LIST = listOf<String>("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val PERIOD_LIST = listOf<String>("AM", "PM")

        fun createHourList() : List<String> {
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

        fun createMinList() : List<String> {
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

        fun convertToMd5(input:String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
        }

        // for MQTT
        const val broker_url = "broker.emqx.io"
        const val broker_port = 1883

        //for device
        const val acDevice = "AC"
        const val lightDevice = "Light"
        const val doorDevice = 2

        // for shared preferences
        const val AUTH_PREF = "Authentication Preferences"
        const val USERNAME_KEY = "9bf2005ca5d03ffb7ced9ebc6d2e9012"
        const val USERNAME = "username"
        const val PASSWORD_KEY = "3180da057ad60735be7f46d493b0ba0a"
        const val PASSWORD = "password"
        const val IS_LOGGED_IN = "isLoggedin"

        const val ROOM_PREF = "Room Preferences"
        const val ROOM_NAME = "room name"
        const val HAVE_YOLO = "have yolo"

        //for API
        const val BASE_URL: String = "https://perfect-cow-14.telebit.io/"

        const val GET_ALL_ROOMS: String = "api/v1/room"

        const val GET_ALL_DEVICES: String = "api/v1/room/device/" //{roomName}

        const val GET_ALL_SCHEDULES: String = "api/v1/room/schedules/" //{roomName}
        const val POST_SCHEDULE: String = "api/v1/schedules"

        const val GET_CONFIG: String = "api/v1/config/" //{deviceName}
        const val POST_CONFIG: String = "api/v1/config/"

        const val GET_SCHEDULER_DETAIL: String = "api/v1/scheduler"

        const val GET_YOLO_DETAIL: String = "api/v1/yolov5/" //{roomName}

        // for MQTT
        const val BROKER = "tcp://broker.emqx.io:1883"
        const val CLIENT_ID = "Android_MQTT_AEDSS"
//        const val USERNAME_MQTT = "pi4"
//        const val PASSWORD_MQTT = "f8a41bcba1561a84f10af0d5851ce93b"

    }
}