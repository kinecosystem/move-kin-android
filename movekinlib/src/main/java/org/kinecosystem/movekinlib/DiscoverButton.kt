package org.kinecosystem.movekin.ecoappsdiscovery.discovery.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.TextView
import org.kinecosystem.movekinlib.R

class DiscoverButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    TextView(context, attrs, defStyleAttr) {
    init {
        text = resources.getString(R.string.discover)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTextAppearance(R.style.kinTextButtonRounded)
        } else {
            setTextAppearance(context, R.style.kinTextButtonRounded)
        }
        background = resources.getDrawable(R.drawable.kin_button_rounded_drawable)
        setOnClickListener {
            context.startActivity(AppsDiscoveryActivity.getIntent(context))
        }
    }
}