package com.rnd.aedss_android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rnd.aedss_android.R
import com.rnd.aedss_android.model.Image

class GalleryAdapter(private val imageList: ArrayList<Image>) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {
    var onImageClick: ((Image) -> Unit)? = null

    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageDate: TextView = itemView.findViewById(R.id.date)
        val image: ImageView = itemView.findViewById(R.id.image)
        val imageItem: CardView = itemView.findViewById(R.id.gallery_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val image = imageList[position]
        holder.imageDate.text = image.date
        Glide.with(holder.itemView.context).load(image.urlImage).into(holder.image);

        holder.imageItem.setOnClickListener {
            onImageClick?.invoke(image)
        }
    }

    override fun getItemCount(): Int {
        if (imageList != null && imageList.size != 0)
            return imageList.size
        else return 0
    }


}