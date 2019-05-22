package org.kinecosystem.transfer.utils

import android.app.Activity
import android.util.DisplayMetrics

class DeviceUtils{
    companion object {

        fun getScreenWidth(context: Activity): Int {
            val displayMetrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }
    }
}