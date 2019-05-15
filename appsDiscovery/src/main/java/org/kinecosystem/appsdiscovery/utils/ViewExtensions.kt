package org.kinecosystem.appsdiscovery.utils

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
    if(isAppInstalled(packageName)){
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        startActivity(intent)
    }
}

fun ImageView.load(url: String) {
    Picasso.get().load(url).into(this)
}