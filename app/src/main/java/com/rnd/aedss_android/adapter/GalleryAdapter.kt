package com.rnd.aedss_android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rnd.aedss_android.R
import com.rnd.aedss_android.viewmodel.SectionImage

class GalleryAdapter(private val sectionList: ArrayList<SectionImage>) :
    RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)
        val imgList: RecyclerView = itemView.findViewById(R.id.img_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.section_gallery_item, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val sectionImage = sectionList[position]
        holder.date.setText(sectionImage.dateSection)

        holder.imgList.setHasFixedSize(true)
        holder.imgList.layoutManager = GridLayoutManager(holder.imgList.context, 3)

        val string: String = sectionImage.imgList.substring(1, sectionImage.imgList.length - 1)
        val imgList = string.split(',').toTypedArray()


    }

    override fun getItemCount(): Int {
        if (sectionList != null && sectionList.size != 0) {
            return sectionList.size
        } else return 0
    }

}