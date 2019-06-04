package org.kinecosystem.common.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics


class DeviceUtils {
    companion object {

        fun getScreenWidth(context: Activity): Int {
            val displayMetrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }

        fun getApplicationName(context: Context): String {
            val stringId = context.applicationInfo.labelRes
            return if (stringId == 0) context.applicationInfo.nonLocalizedLabel.toString() else context.getString(stringId)
        }
    }
}