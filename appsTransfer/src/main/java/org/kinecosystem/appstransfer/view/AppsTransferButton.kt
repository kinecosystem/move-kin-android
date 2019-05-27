package org.kinecosystem.appstransfer.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView
import org.kinecosystem.appstransfer.R

class AppsTransferButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        TextView(context, attrs, defStyleAttr) {

    private fun startAppsDiscoveryActivity() {
       // context.startActivity(AppsDiscoveryActivity.getIntent(context))
        Log.d("", "#### startAppsTransferActivity")
    }

    init {
        text = resources.getString(R.string.transfer_kin)
        TextViewCompat.setTextAppearance(this, R.style.kinTextButtonRounded_Hollow)
        background = ContextCompat.getDrawable(context, R.drawable.kin_button_rounded_hollow_drawable)
        setOnClickListener {
            startAppsDiscoveryActivity()
        }
    }
}