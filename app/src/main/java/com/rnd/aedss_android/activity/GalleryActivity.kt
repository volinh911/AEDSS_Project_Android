package com.rnd.aedss_android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rnd.aedss_android.R
import com.rnd.aedss_android.adapter.GalleryAdapter
import com.rnd.aedss_android.model.Image

class GalleryActivity : AppCompatActivity() {

    private lateinit var imageListRcv: RecyclerView
    private lateinit var imageList: ArrayList<Image>
    private lateinit var galleryAdapter: GalleryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        imageListRcv = findViewById(R.id.gallery_list)
        imageListRcv.setHasFixedSize(true)
        imageListRcv.layoutManager = GridLayoutManager(this, 3)
        imageList = ArrayList()
        imageList.add(Image("25/12/2022", "https://burst.shopifycdn.com/photos/flatlay-iron-skillet-with-meat-and-other-food.jpg?width=1200&format=pjpg&exif=1&iptc=1"))
        imageList.add(Image("24/12/2022", "https://drive.google.com/file/d/1MWEaGf-YAuIASAFAKRXgCnaWe5OJWGZS/view"))
        imageList.add(Image("25/12/2022", "https://burst.shopifycdn.com/photos/flatlay-iron-skillet-with-meat-and-other-food.jpg?width=1200&format=pjpg&exif=1&iptc=1"))
        imageList.add(Image("25/12/2022", "https://burst.shopifycdn.com/photos/flatlay-iron-skillet-with-meat-and-other-food.jpg?width=1200&format=pjpg&exif=1&iptc=1"))
        imageList.add(Image("25/12/2022", "https://burst.shopifycdn.com/photos/flatlay-iron-skillet-with-meat-and-other-food.jpg?width=1200&format=pjpg&exif=1&iptc=1"))
        imageList.add(Image("25/12/2022", "https://burst.shopifycdn.com/photos/flatlay-iron-skillet-with-meat-and-other-food.jpg?width=1200&format=pjpg&exif=1&iptc=1"))
        imageList.add(Image("25/12/2022", "https://burst.shopifycdn.com/photos/flatlay-iron-skillet-with-meat-and-other-food.jpg?width=1200&format=pjpg&exif=1&iptc=1"))
        imageList.add(Image("25/12/2022", "https://burst.shopifycdn.com/photos/flatlay-iron-skillet-with-meat-and-other-food.jpg?width=1200&format=pjpg&exif=1&iptc=1"))

        galleryAdapter = GalleryAdapter(imageList)
        imageListRcv.adapter = galleryAdapter

        galleryAdapter.onImageClick = {
            val intent = Intent(this, ImageViewActivity::class.java)
            intent.putExtra("image", it)
            startActivity(intent)
        }
    }
}