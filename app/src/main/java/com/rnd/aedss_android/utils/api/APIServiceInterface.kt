package com.rnd.aedss_android.utils.api

import com.rnd.aedss_android.datamodel.ConfigData
import com.rnd.aedss_android.datamodel.RoomData
import com.rnd.aedss_android.datamodel.SchedulesData
import com.rnd.aedss_android.datamodel.device_data.DeviceData
import com.rnd.aedss_android.datamodel.device_data.SchedulerData
import com.rnd.aedss_android.datamodel.YoloData
import com.rnd.aedss_android.datamodel.ResponseData
import com.rnd.aedss_android.datamodel.body_model.*
import com.rnd.aedss_android.utils.Constants.Companion.CHANGE_PASS_API
import com.rnd.aedss_android.utils.Constants.Companion.CHECK_EMAIL_API
import com.rnd.aedss_android.utils.Constants.Companion.GET_ALL_DEVICES_API
import com.rnd.aedss_android.utils.Constants.Companion.GET_ALL_ROOMS_API
import com.rnd.aedss_android.utils.Constants.Companion.GET_ALL_SCHEDULES_API
import com.rnd.aedss_android.utils.Constants.Companion.GET_CONFIG_API
import com.rnd.aedss_android.utils.Constants.Companion.GET_SCHEDULER_DETAIL_API
import com.rnd.aedss_android.utils.Constants.Companion.GET_YOLO_DETAIL_API
import com.rnd.aedss_android.utils.Constants.Companion.POST_CONFIG_API
import com.rnd.aedss_android.utils.Constants.Companion.POST_LOGIN_API
import com.rnd.aedss_android.utils.Constants.Companion.POST_SCHEDULE_API
import com.rnd.aedss_android.utils.Constants.Companion.SEND_TOKEN_API
import com.rnd.aedss_android.utils.Constants.Companion.VALIDATE_TOKEN_API
import com.rnd.aedss_android.viewmodel.User
import retrofit2.Call
import retrofit2.http.*

interface APIServiceInterface {
    //user
    @POST(POST_LOGIN_API)
    fun postLogin(
        @Body user: User
    ): Call<ResponseData>

    @POST(CHECK_EMAIL_API)
    fun checkEmail(
        @Body email: String
    ): Call<ResponseData>

    @POST(SEND_TOKEN_API)
    fun sendToken(
        @Body email: String
    ): Call<ResponseData>

    @POST(VALIDATE_TOKEN_API)
    fun validateToken(
        @Body body: PostValidateUserBody
    ): Call<ResponseData>

    @POST(CHANGE_PASS_API)
    fun changePassword(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Body postNewPasswordBody: PostNewPasswordBody
    ): Call<ResponseData>

    //room
    @GET(GET_ALL_ROOMS_API)
    fun getAllRooms(
        @Header("auth") auth: String,
        @Header("userid") userid: String
    ): Call<RoomData>

    //device
    @GET("$GET_ALL_DEVICES_API{roomName}")
    fun getAllDevices(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Path("roomName") roomName: String
    ): Call<List<DeviceData>>

    //Schedule
    @GET("$GET_ALL_SCHEDULES_API{roomName}")
    fun getAllSchedules(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Path("roomName") roomName: String
    ): Call<List<SchedulesData>>

    @POST(POST_SCHEDULE_API)
    fun postSchedule(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Body body: PostScheduleBody
    ): Call<ResponseData>

    @PUT("$POST_SCHEDULE_API/{scheduleID}")
    fun updateSchedule(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Path("scheduleID") scheduleID: String,
        @Body body: UpdateScheduleBody
    ): Call<ResponseData>

    @DELETE("$POST_SCHEDULE_API/{scheduleID}")
    fun deleteSchedule(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Path("scheduleID") scheduleID: String,
    ): Call<ResponseData>

    //Config
    @GET("$GET_CONFIG_API{roomName}")
    fun getConfig(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Path("roomName") roomName: String
    ): Call<ConfigData>

    @POST(POST_CONFIG_API)
    fun postConfig(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Body body: PostConfigBody
    ): Call<ResponseData>

    //scheduler
    @GET(GET_SCHEDULER_DETAIL_API)
    fun getSchedulerDetail(
        @Header("auth") auth: String,
        @Header("userid") userid: String
    ): Call<List<SchedulerData>>

    //yolo
    @GET("$GET_YOLO_DETAIL_API{roomName}")
    fun getYoloDetail(
        @Header("auth") auth: String,
        @Header("userid") userid: String,
        @Path("roomName") roomName: String
    ): Call<List<YoloData>>
}