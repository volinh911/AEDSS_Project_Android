package com.rnd.aedss_android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rnd.aedss_android.R
import com.rnd.aedss_android.viewmodel.Image

class ImageListAdapter(private val imageList: ArrayList<Image>) : RecyclerView.Adapter<ImageListAdapter.ImageListViewHolder>() {
    var onImageClick: ((Image) -> Unit)? = null

    class ImageListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val imageItem: CardView = itemView.findViewById(R.id.gallery_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ImageListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageListViewHolder, position: Int) {
        val image = imageList[position]
        Glide.with(holder.itemView.context)
            .load(image.urlImage)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.image);

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