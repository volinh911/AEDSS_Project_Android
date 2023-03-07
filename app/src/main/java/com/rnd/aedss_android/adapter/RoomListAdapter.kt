package com.rnd.aedss_android.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rnd.aedss_android.viewmodel.Room
import com.rnd.aedss_android.R
import com.rnd.aedss_android.activity.RoomInfoActivity
import com.rnd.aedss_android.databinding.RoomItemBinding
import com.rnd.aedss_android.datamodel.device_data.YoloData
import com.rnd.aedss_android.utils.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomListAdapter(private val context: Context, roomList: List<Room>) :
    RecyclerView.Adapter<RoomListAdapter.RoomListViewHolder>() {

    val list: List<Room> = roomList

    inner class RoomListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomName: TextView = itemView.findViewById(R.id.room_name)
        val roomItem: LinearLayout = itemView.findViewById(R.id.room_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.room_item, parent, false)
        return RoomListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomListViewHolder, position: Int) {
        val room = list[position]
        holder.roomName.text = "Room ${room.name}"
        holder.roomItem.setOnClickListener {
            val intent = Intent(context, RoomInfoActivity::class.java)
            intent.putExtra("room_name", room.name)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}