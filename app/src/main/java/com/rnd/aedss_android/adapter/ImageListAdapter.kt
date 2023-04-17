package com.rnd.aedss_android.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rnd.aedss_android.R
import com.rnd.aedss_android.activity.ImageViewActivity
import com.rnd.aedss_android.activity.RoomInfoActivity
import com.rnd.aedss_android.utils.Constants.Companion.IMG_URL
import com.rnd.aedss_android.viewmodel.Image
import com.rnd.aedss_android.viewmodel.SectionImage

class ImageListAdapter(private val context: Context, imageList: List<Image>) :
    RecyclerView.Adapter<ImageListAdapter.ImageListViewHolder>() {

    val list: List<Image> = imageList

    class ImageListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val imageItem: CardView = itemView.findViewById(R.id.gallery_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ImageListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageListViewHolder, position: Int) {
        val image = list[position]
        var url = IMG_URL + image.urlImage
        Glide.with(holder.itemView.context)
            .load(url)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.image)

        holder.imageItem.setOnClickListener {
            val intent = Intent(context, ImageViewActivity::class.java)
            intent.putExtra("image", url)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        if (list != null && list.isNotEmpty())
            return list.size
        else return 0
    }


}