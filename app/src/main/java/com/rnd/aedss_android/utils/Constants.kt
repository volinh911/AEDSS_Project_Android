package com.rnd.aedss_android.utils

import java.math.BigInteger
import java.security.MessageDigest

public class Constants {
    companion object {
        val DAY_LIST = listOf<String>(
            "No Choice",
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
            for (i in 0 until 24) {
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
        const val AC_DEVICE_FULL = "Air Conditioner"
        const val LIGHT_DEVICE = "Light"
        const val DOOR_DEVICE = "Door"
        const val TURN_OFF = "turn OFF?"
        const val TURN_ON = "turn ON?"

        //for schedule
        const val SCHEDULE = "schedule"
        const val ADD_SCHEDULE = 0
        const val EDIT_SCHEDULE = 1
        const val RCV_SCHEDULE_ID = "scheduleID"
        const val RCV_SCHEDULE_DAY = "rcv day"
        const val RCV_SCHEDULE_DEVICE = "rcv device"
        const val RCV_SCHEDULE_REPEAT = "rcv repeat"
        const val RCV_SCHEDULE_FROM = "rcv from"
        const val RCV_SCHEDULE_TO = "rcv to"

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
        const val DEVICE_LIST = "device list"

        //for API
        const val BASE_URL_API: String = "https://rndaedss.ddns.net/"
        const val POST_LOGIN_API: String = "api/v1/user" //no need token
        const val CHECK_EMAIL_API: String = "api/v1/user/checkemail"
        const val SEND_TOKEN_API: String = "api/v1/user/token"
        const val VALIDATE_TOKEN_API: String = "api/v1/user/validate"
        const val CHANGE_PASS_API: String = "api/v1/user/change"
        const val GET_ALL_ROOMS_API: String = "api/v1/room"
        const val GET_ALL_DEVICES_API: String = "api/v1/room/device/" //{roomName}
        const val GET_ALL_SCHEDULES_API: String = "api/v1/room/schedules/" //{roomName}
        const val POST_SCHEDULE_API: String = "api/v1/schedules"
        const val GET_CONFIG_API: String = "api/v1/config/" //{roomName}
        const val POST_CONFIG_API: String = "api/v1/config/"
        const val GET_SCHEDULER_DETAIL_API: String = "api/v1/scheduler"
        const val GET_YOLO_DETAIL_API: String = "api/v1/yolov5/" //{roomName}

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

        // Push notification Firebase
        const val CHANNEL_ID = "notification_channel"
        const val CHANNEL_NAME = "com.rnd.aedss_android"

        // error
        const val EMPTY_INPUT = "empty"
        const val ERROR_NOTIFY = "error"
        const val ERROR_NOT_EQUAL= "not equal"

        // change/reset pass
        const val PASSWORD_INTENT = "password intent"
        const val CHANGE_PASSWORD = 0
        const val RESET_PASSWORD = 1
    }
}