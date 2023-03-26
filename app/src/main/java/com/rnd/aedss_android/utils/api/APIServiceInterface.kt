package com.rnd.aedss_android.utils.api

import com.rnd.aedss_android.datamodel.ConfigData
import com.rnd.aedss_android.datamodel.RoomData
import com.rnd.aedss_android.datamodel.SchedulesData
import com.rnd.aedss_android.datamodel.device_data.DeviceData
import com.rnd.aedss_android.datamodel.device_data.SchedulerData
import com.rnd.aedss_android.datamodel.device_data.YoloData
import com.rnd.aedss_android.datamodel.ResponseData
import com.rnd.aedss_android.datamodel.body_model.PostConfigBody
import com.rnd.aedss_android.datamodel.body_model.PostScheduleBody
import com.rnd.aedss_android.datamodel.body_model.UpdateScheduleBody
import com.rnd.aedss_android.utils.Constants.Companion.GET_ALL_DEVICES
import com.rnd.aedss_android.utils.Constants.Companion.GET_ALL_ROOMS
import com.rnd.aedss_android.utils.Constants.Companion.GET_ALL_SCHEDULES
import com.rnd.aedss_android.utils.Constants.Companion.GET_CONFIG
import com.rnd.aedss_android.utils.Constants.Companion.GET_SCHEDULER_DETAIL
import com.rnd.aedss_android.utils.Constants.Companion.GET_YOLO_DETAIL
import com.rnd.aedss_android.utils.Constants.Companion.POST_CONFIG
import com.rnd.aedss_android.utils.Constants.Companion.POST_LOGIN
import com.rnd.aedss_android.utils.Constants.Companion.POST_SCHEDULE
import com.rnd.aedss_android.viewmodel.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface APIServiceInterface {
    @POST(POST_LOGIN)
    fun postLogin(
        @Body user: User
    ): Call<ResponseData>

    @GET(GET_ALL_ROOMS)
    fun getAllRooms(
        @Header("auth") auth: String,
        @Header("userid") userid: String
    ): Call<RoomData>

    @GET("$GET_ALL_DEVICES{roomName}")
    fun getAllDevices(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Path("roomName") roomName: String
    ): Call<List<DeviceData>>

    //Schedule
    @GET("$GET_ALL_SCHEDULES{roomName}")
    fun getAllSchedules(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Path("roomName") roomName: String
    ): Call<List<SchedulesData>>

    @POST(POST_SCHEDULE)
    fun postSchedule(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Body body: PostScheduleBody
    ): Call<ResponseData>

    @PUT("$POST_SCHEDULE/{scheduleID}")
    fun updateSchedule(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Path("scheduleID") scheduleID: String,
        @Body body: UpdateScheduleBody
    ): Call<ResponseData>

    @DELETE("$POST_SCHEDULE/{scheduleID}")
    fun deleteSchedule(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Path("scheduleID") scheduleID: String,
    ): Call<ResponseData>

    //Config
    @GET("$GET_CONFIG{roomName}")
    fun getConfig(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Path("roomName") roomName: String
    ): Call<ConfigData>

    @POST(POST_CONFIG)
    fun postConfig(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Body body: PostConfigBody
    ): Call<ResponseData>

    //scheduler
    @GET(GET_SCHEDULER_DETAIL)
    fun getSchedulerDetail(
        @Header("auth") auth: String,
        @Header("userid") userid: String
    ): Call<List<SchedulerData>>

    //yolo
    @GET("$GET_YOLO_DETAIL{roomName}")
    fun getYoloDetail(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Path("roomName") roomName: String
    ): Call<List<YoloData>>
}