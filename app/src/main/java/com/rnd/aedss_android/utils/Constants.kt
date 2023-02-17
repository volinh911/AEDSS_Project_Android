package com.rnd.aedss_android.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

public class Constants {
    companion object {
        val DAY_LIST = listOf<String>("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val PERIOD_LIST = listOf<String>("AM", "PM")

        fun createHourList() : List<String> {
            var hourList = mutableListOf<String>()
            for (i in 1 until 13) {
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

        //for device
        const val acDevice = 0
        const val lightDevice = 1
        const val doorDevice = 2

        // for login
        const val USERNAME_KEY = "9bf2005ca5d03ffb7ced9ebc6d2e9012"
        const val USERNAME = "username"
        const val PASSWORD_KEY = "3180da057ad60735be7f46d493b0ba0a"
        const val PASSWORD = "password"
        const val IS_LOGGED_IN = "isLoggedin"
        const val PREF_NAME = "Authentication Preferences"

        //for API

        const val BASE_URL: String = "https://perfect-cow-14.telebit.io/"

        const val GET_ALL_ROOMS: String = "api/v1/room"

        const val GET_ALL_DEVICES: String = "api/v1/room/device/" //{roomName}

        const val GET_ALL_SCHEDULES: String = "api/v1/room/schedules/" //{roomName}
        const val POST_SCHEDULE: String = "api/v1/schedules"
        // PUT_SCHEDULE: String = "api/v1/schedules/{scheduleID}"
       // DELETE_SCHEDULE: String = "api/v1/schedules/{scheduleID}"

        const val GET_DEVICE_CONFIG: String = "api/v1/config/" //{deviceName}
        const val POST_DEVICE_CONFIG: String = "api/v1/config/"
        // PUT_DEVICE_CONFIG: String = "api/v1/config/{deviceName}"

    }
}