package org.kinecosystem.appsdiscovery.sender.discovery.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.util.AttributeSet
import android.widget.TextView
import org.kinecosystem.appsdiscovery.R

class AppsDiscoveryButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        TextView(context, attrs, defStyleAttr) {

    private fun startAppsDiscoveryActivity() {
        context.startActivity(AppsDiscoveryActivity.getIntent(context))
    }

    init {
        text = resources.getString(R.string.discover)
        TextViewCompat.setTextAppearance(this, R.style.kinTextButtonRounded_Purple)
        background = ContextCompat.getDrawable(context, R.drawable.kin_button_rounded_drawable)
        setOnClickListener {
            startAppsDiscoveryActivity()
        }
    }
}