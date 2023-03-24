package com.rnd.aedss_android.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rnd.aedss_android.utils.Constants
import com.rnd.aedss_android.viewmodel.TimeDetail
import com.rnd.aedss_android.R
import com.rnd.aedss_android.activity.EditTimeActivity
import com.rnd.aedss_android.utils.Constants.Companion.EDIT_SCHEDULE
import com.rnd.aedss_android.utils.Constants.Companion.SCHEDULE

class TimeDetailAdapter(private val context: Context, timeList: List<TimeDetail>) :
    RecyclerView.Adapter<TimeDetailAdapter.TimeDetailViewHoler>() {

    val list: List<TimeDetail> = timeList

    inner class TimeDetailViewHoler(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayText: TextView = itemView.findViewById(R.id.day)
        val deviceIcon: ImageView = itemView.findViewById(R.id.device_icon)
        val repeatIcon: ImageView = itemView.findViewById(R.id.repeat_icon)
        val fromText: TextView = itemView.findViewById(R.id.from_text)
        val toText: TextView = itemView.findViewById(R.id.to_text)
        val timeItem: LinearLayout = itemView.findViewById(R.id.time_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeDetailViewHoler {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.time_item, parent, false)
        return TimeDetailViewHoler(view)
    }

    override fun onBindViewHolder(holder: TimeDetailViewHoler, position: Int) {
        val time = list[position]
        var day: String = ""
        when (time.day) {
            "0" -> {
                day = "Sunday"
            }
            "1" -> {
                day = "Monday"
            }
            "2" -> {
                day = "Tuesday"
            }
            "3" -> {
                day = "Wednesday"
            }
            "4" -> {
                day = "Thursday"
            }
            "5" -> {
                day = "Friday"
            }
            "6" -> {
                day = "Saturday"
            }
            "7" -> {
                day = "Sunday"
            }
        }

        holder.dayText.text = day

        if (time.device == Constants.AC_DEVICE) {
            holder.deviceIcon.setImageResource(R.drawable.ac_icon)
        } else if (time.device == Constants.LIGHT_DEVICE) {
            holder.deviceIcon.setImageResource(R.drawable.light_icon)
        }

        if (time.repeat) {
            holder.repeatIcon.setImageResource(R.drawable.repeat_ic)

        } else {
            holder.repeatIcon.setImageResource(R.drawable.nonrepeat_ic)
        }

        if (time.from == "-1") {
            holder.fromText.visibility = View.GONE
        } else {
            holder.fromText.text = "from: ${time.from}"
        }

        if (time.to == "-1") {
            holder.toText.visibility = View.GONE
        } else {
            holder.toText.text = "to: ${time.to}"
        }

        holder.timeItem.setOnClickListener {
            val intent = Intent(context, EditTimeActivity::class.java)
            intent.putExtra(SCHEDULE, EDIT_SCHEDULE)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}