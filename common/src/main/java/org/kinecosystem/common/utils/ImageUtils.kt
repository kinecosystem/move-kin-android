package org.kinecosystem.common.utils

import com.squareup.picasso.Picasso

class ImageUtils {


    companion object {
        fun fetch(url: String?) {
            if (url != null && url.isNotEmpty()) {
                Picasso.get().load(url).fetch()
            }
        }
    }
}