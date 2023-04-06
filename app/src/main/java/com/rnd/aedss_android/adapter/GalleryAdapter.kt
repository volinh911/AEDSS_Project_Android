package com.rnd.aedss_android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rnd.aedss_android.R
import com.rnd.aedss_android.viewmodel.Image
import com.rnd.aedss_android.viewmodel.SectionImage

class GalleryAdapter(private val context: Context, sectionList: List<SectionImage>) :
    RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    val list: List<SectionImage> = sectionList
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
        val sectionImage = list[position]
        holder.date.setText(sectionImage.dateSection)

        holder.imgList.setHasFixedSize(true)
        holder.imgList.layoutManager = GridLayoutManager(holder.imgList.context, 3)

        var imgLs: MutableList<Image> = mutableListOf()

        var string = sectionImage.imgList.replace("[","")
        string = string.replace("]","")
        string = string.replace(" ","")
        var ls: List<String> = string.split(",").toList()

        for (item in ls) {
            imgLs.add(Image(item))
        }

        holder.imgList.adapter = ImageListAdapter(context, imgLs)
    }

    override fun getItemCount(): Int {
        if (list != null && list.size != 0) {
            return list.size
        } else return 0
    }

}