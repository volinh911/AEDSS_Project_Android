package com.rnd.aedss_android.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rnd.aedss_android.viewmodel.TimeDetail
import com.rnd.aedss_android.R
import com.rnd.aedss_android.adapter.TimeDetailAdapter
import com.rnd.aedss_android.datamodel.SchedulesData
import com.rnd.aedss_android.utils.api.RetrofitInstance
import com.rnd.aedss_android.utils.preferences.AuthenticationPreferences
import com.rnd.aedss_android.utils.preferences.RoomPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TimeListInfoFragment : Fragment() {

    private lateinit var timeListRcv: RecyclerView
    var timeList: MutableList<TimeDetail> = mutableListOf()

    lateinit var roomSession: RoomPreferences
    lateinit var rcvRoom: String

    lateinit var authSession: AuthenticationPreferences
    var auth: String = ""
    var userid: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_time_list_info, container, false)

        roomSession = context?.let { RoomPreferences(it) }!!
        rcvRoom = roomSession.getRoomName()!!

        authSession = context?.let { AuthenticationPreferences(it) }!!
        auth = authSession.getAuthToken().toString()
        userid = authSession.getUserid().toString()

        initScheduleData()

        timeListRcv = view.findViewById(R.id.time_list)
        timeListRcv.setHasFixedSize(true)
        timeListRcv.layoutManager = LinearLayoutManager(context)
        timeListRcv.adapter = TimeDetailAdapter(requireContext(), timeList)

        return view
    }

    fun initScheduleData() {
        RetrofitInstance.apiServiceInterface.getAllSchedules(auth, userid, rcvRoom)
            .enqueue(object : Callback<List<SchedulesData>> {
                override fun onResponse(
                    call: Call<List<SchedulesData>>,
                    response: Response<List<SchedulesData>>
                ) {
                    if (response?.body() == null) {
                        Log.d("Error List Schedule: ", "Error")
                        return
                    }

                    var result = response.body()!!
                    for (item in result) {
                        var day = ""
                        var device = ""
                        var from = ""
                        var to = ""
                        var scheduleId = ""

                        if (item.dayOfTheWeek != null) {
                            day = item.dayOfTheWeek!!
                        }
                        if (item.deviceModule != null) {
                            device = item.deviceModule!!
                        }
                        if (item.timeOn != null) {
                            from = item.timeOn!!
                        }
                        if (item.timeOff != null) {
                            to = item.timeOff!!
                        }

                        if (item._id != null) {
                            scheduleId = item._id!!
                        }

                        if (item.repeat.equals("no")) {
                            timeList.add(TimeDetail(scheduleId, day, device, false, from, to))
                        } else {
                            timeList.add(TimeDetail(scheduleId, day, device, true, from, to))
                        }
                    }

                    timeListRcv.adapter?.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<List<SchedulesData>>, t: Throwable) {
                    Log.d("Error List Schedule: ", "Error")
                }
            })
    }


}