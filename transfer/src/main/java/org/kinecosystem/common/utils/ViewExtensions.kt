package org.kinecosystem.common.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso

fun Context.navigateToUrl(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

fun Context.isAppInstalled(packageName: String): Boolean {
    var found = true
    try {
        packageManager.getPackageInfo(packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        found = false
    }
    return found
}

fun Context.launchApp(packageName: String) {
    if (isAppInstalled(packageName)) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        startActivity(intent)
    }
}

fun ImageView.load(url: String) {
    if (url.isNotEmpty()) {
        Picasso.get().load(url).into(this)
    }
}

fun Context.getApplicationName(): String {
    val stringId = applicationInfo.labelRes
    return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else getString(stringId)
}