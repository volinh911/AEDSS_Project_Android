package com.rnd.aedss_android.utils

import com.rnd.aedss_android.datamodel.RoomData
import com.rnd.aedss_android.datamodel.SchedulesData
import com.rnd.aedss_android.datamodel.device_data.ConfigData
import com.rnd.aedss_android.datamodel.device_data.DeviceData
import com.rnd.aedss_android.utils.Constants.Companion.GET_ALL_DEVICES
import com.rnd.aedss_android.utils.Constants.Companion.GET_ALL_ROOMS
import com.rnd.aedss_android.utils.Constants.Companion.GET_ALL_SCHEDULES
import com.rnd.aedss_android.utils.Constants.Companion.GET_DEVICE_CONFIG
import com.rnd.aedss_android.utils.Constants.Companion.POST_DEVICE_CONFIG
import com.rnd.aedss_android.utils.Constants.Companion.POST_SCHEDULE
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface APIService {
    @GET(GET_ALL_ROOMS)
    suspend fun getAllRooms(): Response<RoomData>

    @GET("$GET_ALL_DEVICES{roomName}")
    suspend fun getAllDevices(
        @Path("roomName") roomName: String
    ): Response<List<DeviceData>>

    //Schedule
    @GET("$GET_ALL_SCHEDULES{roomName}")
    suspend fun getAllSchedules(
        @Path("roomName") roomName: String
    ): Response<List<SchedulesData>>

    @POST(POST_SCHEDULE)
    suspend fun postSchedule(
        @Body scheduleItem: SchedulesData
    ): Response<SchedulesData>

    @PUT("$POST_SCHEDULE/{scheduleID}")
    suspend fun updateSchedule(
        @Path("scheduleID") scheduleID: String,
        @Body scheduleItem: SchedulesData
    ): Response<SchedulesData>

    @DELETE("$POST_SCHEDULE/{scheduleID}")
    suspend fun deleteSchedule(
        @Path("scheduleID") scheduleID: String,
    ): Response<ResponseBody>

    //Config
    @GET("$GET_DEVICE_CONFIG{deviceName}")
    suspend fun getDeviceConfig(
        @Path("deviceName") deviceName: String
    ): Response<List<ConfigData>>

    @POST(POST_DEVICE_CONFIG)
    suspend fun postConfig(
        @Body configItem: ConfigData
    ): Response<ConfigData>
}