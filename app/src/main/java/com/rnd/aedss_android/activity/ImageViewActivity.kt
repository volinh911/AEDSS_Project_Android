package com.rnd.aedss_android.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.rnd.aedss_android.R
import com.rnd.aedss_android.model.Image
import com.rnd.aedss_android.model.Room

class ImageViewActivity : AppCompatActivity() {
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        image = findViewById(R.id.image)
        val rcvImage = intent.getParcelableExtra<Image>("image")
        if (rcvImage != null) {
            Glide.with(this)
                .load(rcvImage.urlImage)
                .into(image)
        }
    }
}