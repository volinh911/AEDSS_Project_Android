package com.rnd.aedss_android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rnd.aedss_android.DBDefine
import com.rnd.aedss_android.model.TimeDetail
import com.rnd.aedss_android.R

class TimeDetailAdapter(private val timeList:ArrayList<TimeDetail>) : RecyclerView.Adapter<TimeDetailAdapter.TimeDetailViewHoler>() {

    var onSettingClick : ((TimeDetail) -> Unit)? = null

    class TimeDetailViewHoler(itemView : View) : RecyclerView.ViewHolder(itemView ){
        val dayText : TextView = itemView.findViewById(R.id.day)
        val deviceIcon : ImageView = itemView.findViewById(R.id.device_icon)
        val repeatIcon : ImageView = itemView.findViewById(R.id.repeat_icon)
        val fromText : TextView = itemView.findViewById(R.id.from_text)
        val toText : TextView = itemView.findViewById(R.id.to_text)
        val setting : ImageView = itemView.findViewById(R.id.setting_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeDetailViewHoler {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.time_item, parent, false)
        return TimeDetailViewHoler(view)
    }

    override fun onBindViewHolder(holder: TimeDetailViewHoler, position: Int) {
        val time = timeList[position]
        holder.dayText.text = time.day

        if (time.devide == DBDefine.acDevice) {
            holder.deviceIcon.setImageResource(R.drawable.ac_icon)
        } else if (time.devide == DBDefine.lightDevice) {
            holder.deviceIcon.setImageResource(R.drawable.light_icon)
        }

        if (time.repeat) {
            holder.repeatIcon.setImageResource(R.drawable.repeat_ic)

        } else {
            holder.repeatIcon.setImageResource(R.drawable.nonrepeat_ic)
        }

        holder.fromText.text = time.from
        holder.toText.text = time.to

        holder.setting.setOnClickListener{
            onSettingClick?.invoke(time)
        }
    }

    override fun getItemCount(): Int {
        if (timeList != null && timeList.size != 0)
            return timeList.size
        else return 0
    }
}