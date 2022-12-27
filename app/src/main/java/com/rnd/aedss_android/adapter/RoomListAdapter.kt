package com.rnd.aedss_android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rnd.aedss_android.model.Room
import com.rnd.aedss_android.R

class RoomListAdapter(private val roomList:ArrayList<Room>) : RecyclerView.Adapter<RoomListAdapter.RoomListViewHolder>() {

    var onRoomClick: ((Room) -> Unit)? = null

    class RoomListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val roomName: TextView = itemView.findViewById(R.id.room_name)
        val roomItem: LinearLayout = itemView.findViewById(R.id.room_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.room_item, parent, false)
        return RoomListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomListViewHolder, position: Int) {
        val room = roomList[position]
        holder.roomName.text = room.name
        holder.roomItem.setOnClickListener{
            onRoomClick?.invoke(room)
        }

    }

    override fun getItemCount(): Int {
        if (roomList != null && roomList.size != 0)
            return roomList.size
        else return 0
    }
}