package org.kinecosystem.movekinlib.utils

import com.squareup.picasso.Picasso

class ImageUtils {


    companion object {
        fun fetch(url:String?){
            if(url != null && !url.isEmpty()) {
                Picasso.get().load(url).fetch()
            }
        }
    }
}