package com.rnd.aedss_android.Model

sealed class Gallery {
    data class Image(val imageUrl: String): Gallery()
    data class ImageHeading(val dateString: String): Gallery()
}
