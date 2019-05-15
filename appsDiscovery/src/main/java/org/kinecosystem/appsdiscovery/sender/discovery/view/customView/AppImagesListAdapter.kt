package org.kinecosystem.appsdiscovery.sender.discovery.view.customView

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import org.kinecosystem.appsdiscovery.R
import org.kinecosystem.common.utils.DeviceUtils
import org.kinecosystem.common.utils.load

class AppImagesListAdapter(private val context: Context, private val imagesUrl:List<String>): RecyclerView.Adapter<AppImagesListAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AppImagesListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.app_image_item, parent, false)
        val maxWidth = DeviceUtils.getScreenWidth(context as Activity)
        view.layoutParams.width = (maxWidth * 0.67).toInt()
        return ViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imagesUrl[position])
    }

    override fun getItemCount(): Int {
        return imagesUrl.size
    }

    class ViewHolder(private val context: Context, val view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.appImage)

        fun bind(imageUrl: String) {
            imageView.load(imageUrl)
        }
    }
}