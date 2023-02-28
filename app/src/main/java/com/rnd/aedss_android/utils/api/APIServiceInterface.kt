package com.rnd.aedss_android.utils.api

import com.rnd.aedss_android.datamodel.RoomData
import com.rnd.aedss_android.datamodel.SchedulesData
import com.rnd.aedss_android.datamodel.ConfigData
import com.rnd.aedss_android.datamodel.device_data.DeviceData
import com.rnd.aedss_android.datamodel.device_data.SchedulerData
import com.rnd.aedss_android.datamodel.device_data.YoloData
import com.rnd.aedss_android.utils.Constants.Companion.GET_ALL_DEVICES
import com.rnd.aedss_android.utils.Constants.Companion.GET_ALL_ROOMS
import com.rnd.aedss_android.utils.Constants.Companion.GET_ALL_SCHEDULES
import com.rnd.aedss_android.utils.Constants.Companion.GET_CONFIG
import com.rnd.aedss_android.utils.Constants.Companion.GET_SCHEDULER_DETAIL
import com.rnd.aedss_android.utils.Constants.Companion.GET_YOLO_DETAIL
import com.rnd.aedss_android.utils.Constants.Companion.POST_CONFIG
import com.rnd.aedss_android.utils.Constants.Companion.POST_SCHEDULE
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface APIServiceInterface {
    @GET(GET_ALL_ROOMS)
    fun getAllRooms(): Call<RoomData>

    @GET("$GET_ALL_DEVICES{roomName}")
    fun getAllDevices(
        @Path("roomName") roomName: String
    ): Call<List<DeviceData>>

    //Schedule
    @GET("$GET_ALL_SCHEDULES{roomName}")
    fun getAllSchedules(
        @Path("roomName") roomName: String
    ): Call<List<SchedulesData>>

    @POST(POST_SCHEDULE)
    fun postSchedule(
        @Body scheduleItem: SchedulesData
    ): Call<SchedulesData>

    @PUT("$POST_SCHEDULE/{scheduleID}")
    fun updateSchedule(
        @Path("scheduleID") scheduleID: String,
        @Body scheduleItem: SchedulesData
    ): Call<SchedulesData>

    @DELETE("$POST_SCHEDULE/{scheduleID}")
    fun deleteSchedule(
        @Path("scheduleID") scheduleID: String,
    ): Call<ResponseBody>

    //Config
    @GET("$GET_CONFIG{roomName}")
    fun getConfig(
        @Path("roomName") roomName: String
    ): Call<ConfigData>

    @POST(POST_CONFIG)
    fun postConfig(
        @Body configItem: ConfigData
    ): Call<ConfigData>

    //scheduler
    @GET(GET_SCHEDULER_DETAIL)
    fun getSchedulerDetail(): Call<List<SchedulerData>>

    //yolo
    @GET("$GET_YOLO_DETAIL{roomName}")
    fun getYoloDetail(
        @Path("roomName") roomName: String
    ): Call<List<YoloData>>
}