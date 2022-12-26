package com.rnd.aedss_android.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rnd.aedss_android.DBDefine
import com.rnd.aedss_android.Model.TimeDetail
import com.rnd.aedss_android.R
import com.rnd.aedss_android.activity.EditTimeActivity
import com.rnd.aedss_android.adapter.TimeDetailAdapter

class TimeListInfoFragment : Fragment() {

    private lateinit var timeListRcv: RecyclerView
    private lateinit var timeList: ArrayList<TimeDetail>
    private lateinit var timeDetailAdapter: TimeDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_time_list_info, container, false)

        timeListRcv = view.findViewById(R.id.time_list)
        timeListRcv.setHasFixedSize(true)
        timeListRcv.layoutManager = LinearLayoutManager(context)
        timeList = ArrayList()
        timeList.add(TimeDetail("Monday", DBDefine.acDevice, true, "10:00", "11:00"))
        timeList.add(TimeDetail("Thursday", DBDefine.lightDevice, false, "16:00", "17:00"))
        timeDetailAdapter = TimeDetailAdapter(timeList)
        timeListRcv.adapter = timeDetailAdapter

        timeDetailAdapter.onSettingClick = {
            val intent = Intent(context, EditTimeActivity::class.java)
            intent.putExtra("title", "Edit Time")
            startActivity(intent)
        }
        return view
    }

}