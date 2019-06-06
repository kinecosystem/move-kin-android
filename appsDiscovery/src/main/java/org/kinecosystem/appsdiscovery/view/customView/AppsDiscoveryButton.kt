package org.kinecosystem.appsdiscovery.view.customView

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.util.AttributeSet
import android.widget.TextView
import org.kinecosystem.appsdiscovery.R
import org.kinecosystem.appsdiscovery.view.AppsDiscoveryActivity

class AppsDiscoveryButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        TextView(context, attrs, defStyleAttr) {

    private fun startAppsDiscoveryActivity() {
        context.startActivity(AppsDiscoveryActivity.getIntent(context))
    }

    init {
        text = resources.getString(R.string.explore_kin)
        TextViewCompat.setTextAppearance(this, R.style.KinTextButtonRounded_Purple)
        background = ContextCompat.getDrawable(context, R.drawable.kin_button_rounded_drawable)
        setOnClickListener {
            startAppsDiscoveryActivity()
        }
    }
}